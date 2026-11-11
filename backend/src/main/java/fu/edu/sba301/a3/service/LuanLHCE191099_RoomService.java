package fu.edu.sba301.a3.service;

import fu.edu.sba301.a3.dto.LuanLHCE191099_RoomDeleteResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RoomRequest;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RoomTypeResponse;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import fu.edu.sba301.a3.entity.LuanLHCE191099_RoomType;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.LuanLHCE191099_BookingDetailRepository;
import fu.edu.sba301.a3.repository.LuanLHCE191099_RoomRepository;
import fu.edu.sba301.a3.repository.LuanLHCE191099_RoomTypeRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LuanLHCE191099_RoomService {

    private final LuanLHCE191099_RoomRepository roomRepository;
    private final LuanLHCE191099_RoomTypeRepository roomTypeRepository;
    private final LuanLHCE191099_BookingDetailRepository bookingDetailRepository;

    public List<LuanLHCE191099_Room> getRooms(
        String searchText,
        boolean activeOnly,
        LocalDate startDate,
        LocalDate endDate
    ) {
        boolean hasStartDate = startDate != null;
        boolean hasEndDate = endDate != null;
        if (hasStartDate != hasEndDate || (hasStartDate && !endDate.isAfter(startDate))) {
            throw new LuanLHCE191099_ApiException(400, "Provide a valid start and end date together.");
        }

        String keyword = searchText.trim().toLowerCase(Locale.ROOT);
        List<LuanLHCE191099_Room> rooms = roomRepository.findAll(Sort.by("number"));
        List<LuanLHCE191099_Room> matchingRooms = new ArrayList<>();

        for (LuanLHCE191099_Room room : rooms) {
            if (activeOnly && room.getStatus() != 1) {
                continue;
            }

            String roomText = room.getNumber() + " " + room.getType().getName() + " " + room.getDescription();
            if (!roomText.toLowerCase(Locale.ROOT).contains(keyword)) {
                continue;
            }

            if (hasStartDate && bookingDetailRepository.hasOverlap(room.getId(), startDate, endDate, null)) {
                continue;
            }

            matchingRooms.add(room);
        }
        return matchingRooms;
    }

    public LuanLHCE191099_Room getRoomById(Long id, boolean publicOnly) {
        LuanLHCE191099_Room room = roomRepository.findById(id)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Room not found."));
        if (publicOnly && room.getStatus() != 1) {
            throw new LuanLHCE191099_ApiException(404, "Room not found.");
        }
        return room;
    }

    public List<LuanLHCE191099_RoomTypeResponse> getRoomTypes() {
        List<LuanLHCE191099_RoomType> roomTypes = roomTypeRepository.findAll(Sort.by("id"));
        List<LuanLHCE191099_RoomTypeResponse> responses = new ArrayList<>();

        for (LuanLHCE191099_RoomType roomType : roomTypes) {
            LuanLHCE191099_RoomTypeResponse response = LuanLHCE191099_RoomTypeResponse.builder()
                .id(roomType.getId())
                .name(roomType.getName())
                .description(roomType.getDescription())
                .note(roomType.getNote())
                .build();
            responses.add(response);
        }
        return responses;
    }

    @Transactional
    public LuanLHCE191099_Room createRoom(LuanLHCE191099_RoomRequest request) {
        String number = request.getNumber().trim();
        validateRoomNumber(number, null);

        LuanLHCE191099_Room room = LuanLHCE191099_Room.builder()
            .number(number)
            .description(request.getDescription())
            .capacity(request.getCapacity())
            .status(request.getStatus())
            .price(request.getPrice())
            .type(findRoomTypeById(request.getRoomTypeId()))
            .build();

        return roomRepository.save(room);
    }

    @Transactional
    public LuanLHCE191099_Room updateRoom(Long id, LuanLHCE191099_RoomRequest request) {
        LuanLHCE191099_Room room = findRoomAndLock(id);
        String number = request.getNumber().trim();
        validateRoomNumber(number, id);
        LuanLHCE191099_RoomType roomType = findRoomTypeById(request.getRoomTypeId());

        room.setNumber(number);
        room.setDescription(request.getDescription());
        room.setCapacity(request.getCapacity());
        room.setStatus(request.getStatus());
        room.setPrice(request.getPrice());
        room.setType(roomType);

        return roomRepository.save(room);
    }

    @Transactional
    public LuanLHCE191099_RoomDeleteResponse deleteRoom(Long id) {
        LuanLHCE191099_Room room = findRoomAndLock(id);
        boolean hasBookingHistory = bookingDetailRepository.existsByRoom_Id(id);
        String message;

        if (hasBookingHistory) {
            room.setStatus(0);
            roomRepository.save(room);
            message = "Room has booking history and was set inactive.";
        } else {
            roomRepository.delete(room);
            message = "Room deleted.";
        }

        return LuanLHCE191099_RoomDeleteResponse.builder()
            .message(message)
            .deactivated(hasBookingHistory)
            .build();
    }

    private void validateRoomNumber(String number, Long roomId) {
        boolean numberUsedByAnotherRoom = roomRepository.findByNumber(number)
            .filter(other -> !other.getId().equals(roomId))
            .isPresent();
        if (numberUsedByAnotherRoom) {
            throw new LuanLHCE191099_ApiException(409, "Room number already exists.");
        }
    }

    private LuanLHCE191099_RoomType findRoomTypeById(Long roomTypeId) {
        return roomTypeRepository.findById(roomTypeId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Room type not found."));
    }

    private LuanLHCE191099_Room findRoomAndLock(Long id) {
        return roomRepository.findLockedById(id)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Room not found."));
    }
}
