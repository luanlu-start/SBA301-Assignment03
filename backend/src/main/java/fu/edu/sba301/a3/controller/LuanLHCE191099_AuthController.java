package fu.edu.sba301.a3.controller;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_AuthResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_LoginRequest;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RegisterRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a3.service.LuanLHCE191099_AuthService;
import fu.edu.sba301.a3.service.LuanLHCE191099_CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class LuanLHCE191099_AuthController {

    private final LuanLHCE191099_AuthService authService;
    private final LuanLHCE191099_CustomerService customerService;

    @PostMapping("/login")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_AuthResponse>> login(
        @Valid @RequestBody LuanLHCE191099_LoginRequest request
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/register")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Customer>> registerCustomer(
        @Valid @RequestBody LuanLHCE191099_RegisterRequest request
    ) {
        LuanLHCE191099_Customer customer = customerService.registerCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LuanLHCE191099_ApiResponse.ok("Customer registered successfully.", customer));
    }

    @GetMapping("/me")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_UserPrincipal>> getCurrentUser(
        @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(user));
    }
}
