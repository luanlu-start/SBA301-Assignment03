package fu.edu.sba301.a3.service;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.entity.*;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.*;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_BookingService {

  private final LuanLHCE191099_BookingRepository bookings;
  private final LuanLHCE191099_CustomerRepository customers;
  private final LuanLHCE191099_RoomRepository rooms;
  private final LuanLHCE191099_BookingDetailRepository details;

  public LuanLHCE191099_BookingService(
    LuanLHCE191099_BookingRepository bookings,
    LuanLHCE191099_CustomerRepository customers,
    LuanLHCE191099_RoomRepository rooms,
    LuanLHCE191099_BookingDetailRepository details
  ) {
    this.bookings = bookings;
    this.customers = customers;
    this.rooms = rooms;
    this.details = details;
  }

  public List<BookingResponse> list(String q, Long owner) {
    String k = q.trim().toLowerCase(Locale.ROOT);
    return (
      owner == null
        ? bookings.findAll(Sort.by(Sort.Direction.DESC, "id"))
        : bookings.findByCustomer_IdOrderByIdDesc(owner)
    )
      .stream()
      .filter(b ->
        (b.getId() + " " + b.getCustomer().getFullName() + " " + b.getCustomer().getEmail())
          .toLowerCase(Locale.ROOT)
          .contains(k)
      )
      .map(LuanLHCE191099_BookingService::view)
      .toList();
  }

  public BookingResponse get(Long id, Long owner) {
    LuanLHCE191099_Booking b = required(id);
    if (
      owner != null && !b.getCustomer().getId().equals(owner)
    ) throw new LuanLHCE191099_ApiException(403, "This booking belongs to another customer.");
    return view(b);
  }

  @Transactional
  public BookingResponse create(BookingInput input, LuanLHCE191099_UserPrincipal actor) {
    Long customerId = input.customerId();
    if (actor.role().equals("CUSTOMER")) {
      if (
        customerId != null && !customerId.equals(actor.id())
      ) throw new LuanLHCE191099_ApiException(403, "You can book only for your own account.");
      if (input.status() != 1) throw new LuanLHCE191099_ApiException(
        400,
        "A new online reservation must be confirmed."
      );
      customerId = actor.id();
    }
    if (customerId == null) throw new LuanLHCE191099_ApiException(400, "Select a customer.");
    LuanLHCE191099_Booking b = new LuanLHCE191099_Booking();
    b.setBookingDate(LocalDateTime.now());
    apply(b, input, customerId);
    return view(bookings.saveAndFlush(b));
  }

  @Transactional
  public BookingResponse update(Long id, BookingInput input) {
    LuanLHCE191099_Booking b = bookings
      .findLockedById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Booking not found."));
    if (input.customerId() == null) throw new LuanLHCE191099_ApiException(
      400,
      "Select a customer."
    );
    apply(b, input, input.customerId());
    return view(bookings.saveAndFlush(b));
  }

  @Transactional
  public void delete(Long id) {
    LuanLHCE191099_Booking b = bookings
      .findLockedById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Booking not found."));
    bookings.delete(b);
    bookings.flush();
  }

  private void apply(LuanLHCE191099_Booking b, BookingInput input, Long customerId) {
    if (!input.endDate().isAfter(input.startDate())) throw new LuanLHCE191099_ApiException(
      400,
      "Check-out must be after check-in."
    );
    boolean sameHistoricStart =
      b.getId() != null &&
      !b.getDetails().isEmpty() &&
      b.getDetails().getFirst().getStartDate().equals(input.startDate());
    if (
      input.startDate().isBefore(LocalDate.now()) && !sameHistoricStart
    ) throw new LuanLHCE191099_ApiException(400, "A new check-in date cannot be in the past.");
    Set<Long> ids = new TreeSet<>(input.roomIds());
    if (ids.size() != input.roomIds().size()) throw new LuanLHCE191099_ApiException(
      400,
      "Select each room only once."
    );
    LuanLHCE191099_Customer customer = customers
      .findById(customerId)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Customer not found."));
    if (input.status() == 1 && customer.getStatus() != 1) throw new LuanLHCE191099_ApiException(
      409,
      "Customer is inactive."
    );
    List<LuanLHCE191099_Room> selected = new ArrayList<>();
    for (Long roomId : ids) {
      LuanLHCE191099_Room room = rooms
        .findLockedById(roomId)
        .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Room not found."));
      if (input.status() == 1 && room.getStatus() != 1) throw new LuanLHCE191099_ApiException(
        409,
        "Room " + room.getNumber() + " is inactive."
      );
      if (
        input.status() == 1 &&
        details.countOverlap(roomId, input.startDate(), input.endDate(), b.getId()) > 0
      ) throw new LuanLHCE191099_ApiException(
        409,
        "Room " + room.getNumber() + " is already booked for these dates."
      );
      selected.add(room);
    }
    b.setCustomer(customer);
    b.setStatus(input.status());
    Map<Long, LuanLHCE191099_BookingDetail> existing = new HashMap<>();
    b.getDetails().forEach(d -> existing.put(d.getRoom().getId(), d));
    b.getDetails().removeIf(d -> !ids.contains(d.getRoom().getId()));
    BigDecimal total = BigDecimal.ZERO;
    long nights = ChronoUnit.DAYS.between(input.startDate(), input.endDate());
    for (LuanLHCE191099_Room room : selected) {
      LuanLHCE191099_BookingDetail detail = existing.get(room.getId());
      if (detail == null) {
        detail = new LuanLHCE191099_BookingDetail();
        detail.setId(new LuanLHCE191099_BookingDetailId(b.getId(), room.getId()));
        detail.setReservation(b);
        detail.setRoom(room);
        b.getDetails().add(detail);
      }
      detail.setStartDate(input.startDate());
      detail.setEndDate(input.endDate());
      detail.setActualPrice(room.getPrice());
      total = total.add(room.getPrice().multiply(BigDecimal.valueOf(nights)));
    }
    b.setTotalPrice(total);
  }

  private LuanLHCE191099_Booking required(Long id) {
    return bookings
      .findById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Booking not found."));
  }

  public static BookingResponse view(LuanLHCE191099_Booking b) {
    return new BookingResponse(
      b.getId(),
      b.getBookingDate(),
      b.getTotalPrice(),
      b.getCustomer().getId(),
      b.getCustomer().getFullName(),
      b.getCustomer().getEmail(),
      b.getStatus(),
      b
        .getDetails()
        .stream()
        .map(d ->
          new DetailResponse(
            d.getRoom().getId(),
            d.getRoom().getNumber(),
            d.getStartDate(),
            d.getEndDate(),
            d.getActualPrice(),
            d
              .getActualPrice()
              .multiply(
                BigDecimal.valueOf(ChronoUnit.DAYS.between(d.getStartDate(), d.getEndDate()))
              )
          )
        )
        .toList()
    );
  }
}
