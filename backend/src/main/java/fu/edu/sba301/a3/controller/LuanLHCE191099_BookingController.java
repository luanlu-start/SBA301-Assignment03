package fu.edu.sba301.a3.controller;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_BookingRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Booking;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a3.service.LuanLHCE191099_BookingService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bookings")
public class LuanLHCE191099_BookingController {

    private final LuanLHCE191099_BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<LuanLHCE191099_ApiResponse<List<LuanLHCE191099_Booking>>> getBookings(
        @RequestParam(name = "q", defaultValue = "") String searchText
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(bookingService.getBookings(searchText, null)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF','CUSTOMER')")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Booking>> getBookingById(
        @PathVariable Long id,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        Long ownerId = null;
        if (user.role().equals("CUSTOMER")) {
            ownerId = user.id();
        }
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(bookingService.getBookingById(id, ownerId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF','CUSTOMER')")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Booking>> createBooking(
        @Valid @RequestBody LuanLHCE191099_BookingRequest request,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        LuanLHCE191099_Booking booking = bookingService.createBooking(request, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(LuanLHCE191099_ApiResponse.ok("Reservation created.", booking));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('STAFF')")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Booking>> updateBooking(
        @PathVariable Long id,
        @Valid @RequestBody LuanLHCE191099_BookingRequest request
    ) {
        LuanLHCE191099_Booking booking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok("Reservation updated.", booking));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('STAFF')")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }
}
