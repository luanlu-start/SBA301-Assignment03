package fu.edu.sba301.a3.controller;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_ProfileRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Booking;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a3.service.LuanLHCE191099_BookingService;
import fu.edu.sba301.a3.service.LuanLHCE191099_CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/my")
@PreAuthorize("hasRole('CUSTOMER')")
public class LuanLHCE191099_MyController {

    private final LuanLHCE191099_CustomerService customerService;
    private final LuanLHCE191099_BookingService bookingService;

    @GetMapping("/profile")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Customer>> getProfile(
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(customerService.getCustomerById(user.id())));
    }

    @PutMapping("/profile")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Customer>> updateProfile(
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user,
        @Valid @RequestBody LuanLHCE191099_ProfileRequest request
    ) {
        LuanLHCE191099_Customer customer = customerService.updateProfile(user.id(), request);
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok("Profile updated successfully.", customer));
    }

    @GetMapping("/bookings")
    public ResponseEntity<LuanLHCE191099_ApiResponse<List<LuanLHCE191099_Booking>>> getMyBookings(
        @RequestParam(name = "q", defaultValue = "") String searchText,
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(bookingService.getBookings(searchText, user.id())));
    }
}
