package fu.edu.sba301.a3.controller;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RoomDeleteResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RoomRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import fu.edu.sba301.a3.service.LuanLHCE191099_RoomService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_RoomController {

    private final LuanLHCE191099_RoomService roomService;

    @GetMapping
    public ResponseEntity<LuanLHCE191099_ApiResponse<List<LuanLHCE191099_Room>>> getRooms(
        @RequestParam(name = "q", defaultValue = "") String searchText
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(roomService.getRooms(searchText, false, null, null)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Room>> getRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(roomService.getRoomById(id, false)));
    }

    @PostMapping
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Room>> createRoom(
        @Valid @RequestBody LuanLHCE191099_RoomRequest request
    ) {
        LuanLHCE191099_Room room = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LuanLHCE191099_ApiResponse.ok("Room created successfully.", room));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Room>> updateRoom(
        @PathVariable Long id,
        @Valid @RequestBody LuanLHCE191099_RoomRequest request
    ) {
        LuanLHCE191099_Room room = roomService.updateRoom(id, request);
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok("Room updated successfully.", room));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_RoomDeleteResponse>> deleteRoom(@PathVariable Long id) {
        LuanLHCE191099_RoomDeleteResponse result = roomService.deleteRoom(id);
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(result.getMessage(), result));
    }
}
