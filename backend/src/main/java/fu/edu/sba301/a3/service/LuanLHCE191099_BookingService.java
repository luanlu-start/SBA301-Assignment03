package fu.edu.sba301.a3.service;

import fu.edu.sba301.a3.dto.LuanLHCE191099_BookingRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Booking;
import fu.edu.sba301.a3.entity.LuanLHCE191099_BookingDetail;
import fu.edu.sba301.a3.entity.LuanLHCE191099_BookingDetailId;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.LuanLHCE191099_BookingDetailRepository;
import fu.edu.sba301.a3.repository.LuanLHCE191099_BookingRepository;
import fu.edu.sba301.a3.repository.LuanLHCE191099_CustomerRepository;
import fu.edu.sba301.a3.repository.LuanLHCE191099_RoomRepository;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LuanLHCE191099_BookingService {

    private final LuanLHCE191099_BookingRepository bookingRepository;
    private final LuanLHCE191099_CustomerRepository customerRepository;
    private final LuanLHCE191099_RoomRepository roomRepository;
    private final LuanLHCE191099_BookingDetailRepository bookingDetailRepository;

    public List<LuanLHCE191099_Booking> getBookings(String searchText, Long ownerId) {
        List<LuanLHCE191099_Booking> bookings;
        if (ownerId == null) {
            bookings = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        } else {
            bookings = bookingRepository.findByCustomer_IdOrderByIdDesc(ownerId);
        }

        String keyword = searchText.trim().toLowerCase(Locale.ROOT);
        List<LuanLHCE191099_Booking> matchingBookings = new ArrayList<>();
        for (LuanLHCE191099_Booking booking : bookings) {
            LuanLHCE191099_Customer customer = booking.getCustomer();
            String bookingText = booking.getId() + " " + customer.getFullName() + " " + customer.getEmail();
            if (bookingText.toLowerCase(Locale.ROOT).contains(keyword)) {
                matchingBookings.add(booking);
            }
        }
        return matchingBookings;
    }

    public LuanLHCE191099_Booking getBookingById(Long id, Long ownerId) {
        LuanLHCE191099_Booking booking = bookingRepository.findById(id)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Booking not found."));

        if (ownerId != null && !ownerId.equals(booking.getCustomer().getId())) {
            throw new LuanLHCE191099_ApiException(403, "This booking belongs to another customer.");
        }
        return booking;
    }

    @Transactional
    public LuanLHCE191099_Booking createBooking(
        LuanLHCE191099_BookingRequest request,
        LuanLHCE191099_UserPrincipal currentUser
    ) {
        Long customerId = request.getCustomerId();
        if (currentUser.role().equals("CUSTOMER")) {
            if (customerId != null && !customerId.equals(currentUser.id())) {
                throw new LuanLHCE191099_ApiException(403, "You can book only for your own account.");
            }
            if (request.getStatus() != 1) {
                throw new LuanLHCE191099_ApiException(400, "A new online reservation must be confirmed.");
            }
            customerId = currentUser.id();
        }
        if (customerId == null) {
            throw new LuanLHCE191099_ApiException(400, "Select a customer.");
        }

        LuanLHCE191099_Booking booking = LuanLHCE191099_Booking.builder()
            .bookingDate(LocalDateTime.now())
            .build();
        applyBookingRequest(booking, request, customerId);

        return bookingRepository.save(booking);
    }

    @Transactional
    public LuanLHCE191099_Booking updateBooking(Long id, LuanLHCE191099_BookingRequest request) {
        LuanLHCE191099_Booking booking = findBookingAndLock(id);
        Long customerId = request.getCustomerId();
        if (customerId == null) {
            throw new LuanLHCE191099_ApiException(400, "Select a customer.");
        }

        applyBookingRequest(booking, request, customerId);
        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(Long id) {
        LuanLHCE191099_Booking booking = findBookingAndLock(id);
        bookingRepository.delete(booking);
    }

    private void applyBookingRequest(
        LuanLHCE191099_Booking booking,
        LuanLHCE191099_BookingRequest request,
        Long customerId
    ) {
        validateDates(booking, request);

        // Lock rooms in ascending ID order for every reservation.
        Set<Long> selectedRoomIds = new TreeSet<>(request.getRoomIds());
        if (selectedRoomIds.size() != request.getRoomIds().size()) {
            throw new LuanLHCE191099_ApiException(400, "Select each room only once.");
        }

        LuanLHCE191099_Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Customer not found."));
        if (request.getStatus() == 1 && customer.getStatus() != 1) {
            throw new LuanLHCE191099_ApiException(409, "Customer is inactive.");
        }

        List<LuanLHCE191099_Room> selectedRooms = loadSelectedRooms(
            selectedRoomIds, request, booking.getId()
        );

        booking.setCustomer(customer);
        booking.setStatus(request.getStatus());
        updateBookingDetails(booking, request, selectedRoomIds, selectedRooms);
    }

    private void validateDates(
        LuanLHCE191099_Booking booking,
        LuanLHCE191099_BookingRequest request
    ) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {
            throw new LuanLHCE191099_ApiException(400, "Check-out must be after check-in.");
        }

        boolean keepingOriginalCheckIn = booking.getId() != null
            && !booking.getDetails().isEmpty()
            && booking.getDetails().getFirst().getStartDate().equals(request.getStartDate());

        if (request.getStartDate().isBefore(LocalDate.now()) && !keepingOriginalCheckIn) {
            throw new LuanLHCE191099_ApiException(400, "A new check-in date cannot be in the past.");
        }
    }

    private List<LuanLHCE191099_Room> loadSelectedRooms(
        Set<Long> selectedRoomIds,
        LuanLHCE191099_BookingRequest request,
        Long bookingId
    ) {
        List<LuanLHCE191099_Room> selectedRooms = new ArrayList<>();
        boolean confirmed = request.getStatus() == 1;

        for (Long roomId : selectedRoomIds) {
            LuanLHCE191099_Room room = roomRepository.findLockedById(roomId)
                .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Room not found."));

            if (confirmed) {
                if (room.getStatus() != 1) {
                    throw new LuanLHCE191099_ApiException(409, "Room " + room.getNumber() + " is inactive.");
                }

                boolean alreadyBooked = bookingDetailRepository.hasOverlap(
                    roomId, request.getStartDate(), request.getEndDate(), bookingId
                );
                if (alreadyBooked) {
                    throw new LuanLHCE191099_ApiException(
                        409, "Room " + room.getNumber() + " is already booked for these dates."
                    );
                }
            }
            selectedRooms.add(room);
        }
        return selectedRooms;
    }

    private void updateBookingDetails(
        LuanLHCE191099_Booking booking,
        LuanLHCE191099_BookingRequest request,
        Set<Long> selectedRoomIds,
        List<LuanLHCE191099_Room> selectedRooms
    ) {
        Map<Long, LuanLHCE191099_BookingDetail> existingDetailsByRoomId = new HashMap<>();
        for (LuanLHCE191099_BookingDetail detail : booking.getDetails()) {
            existingDetailsByRoomId.put(detail.getRoom().getId(), detail);
        }

        booking.getDetails().removeIf(detail -> !selectedRoomIds.contains(detail.getRoom().getId()));

        long nights = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (LuanLHCE191099_Room room : selectedRooms) {
            LuanLHCE191099_BookingDetail detail = existingDetailsByRoomId.get(room.getId());
            if (detail == null) {
                LuanLHCE191099_BookingDetailId detailId = LuanLHCE191099_BookingDetailId.builder()
                    .reservationId(booking.getId())
                    .roomId(room.getId())
                    .build();
                detail = LuanLHCE191099_BookingDetail.builder()
                    .id(detailId)
                    .reservation(booking)
                    .room(room)
                    .build();
                booking.getDetails().add(detail);
            }

            BigDecimal pricePerNight = room.getPrice();
            detail.setStartDate(request.getStartDate());
            detail.setEndDate(request.getEndDate());
            detail.setActualPrice(pricePerNight);

            BigDecimal roomTotal = pricePerNight.multiply(BigDecimal.valueOf(nights));
            totalPrice = totalPrice.add(roomTotal);
        }
        booking.setTotalPrice(totalPrice);
    }

    private LuanLHCE191099_Booking findBookingAndLock(Long id) {
        return bookingRepository.findLockedById(id)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Booking not found."));
    }
}
