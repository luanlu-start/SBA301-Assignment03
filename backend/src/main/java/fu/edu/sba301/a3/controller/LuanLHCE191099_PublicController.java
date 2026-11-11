package fu.edu.sba301.a3.controller;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RoomTypeResponse;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import fu.edu.sba301.a3.service.LuanLHCE191099_RoomService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public")
public class LuanLHCE191099_PublicController {

    private final LuanLHCE191099_RoomService roomService;

    @GetMapping("/rooms")
    public ResponseEntity<LuanLHCE191099_ApiResponse<List<LuanLHCE191099_Room>>> getPublicRooms(
        @RequestParam(name = "q", defaultValue = "") String searchText,
        @RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
        ) LocalDate startDate,
        @RequestParam(required = false) @DateTimeFormat(
            iso = DateTimeFormat.ISO.DATE
        ) LocalDate endDate
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(roomService.getRooms(searchText, true, startDate, endDate)));
    }

    @GetMapping("/rooms/{id}")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Room>> getPublicRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(roomService.getRoomById(id, true)));
    }

    @GetMapping("/room-types")
    public ResponseEntity<LuanLHCE191099_ApiResponse<List<LuanLHCE191099_RoomTypeResponse>>> getRoomTypes() {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(roomService.getRoomTypes()));
    }
}
