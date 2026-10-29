package fu.edu.sba301.a3.controller;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a3.service.LuanLHCE191099_AuthService;
import fu.edu.sba301.a3.service.LuanLHCE191099_CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LuanLHCE191099_AuthController {

  private final LuanLHCE191099_AuthService auth;
  private final LuanLHCE191099_CustomerService customers;

  public LuanLHCE191099_AuthController(
    LuanLHCE191099_AuthService auth,
    LuanLHCE191099_CustomerService customers
  ) {
    this.auth = auth;
    this.customers = customers;
  }

  @PostMapping("/login")
  public AuthResponse login(@Valid @RequestBody LoginInput input) {
    return auth.login(input);
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse register(@Valid @RequestBody RegisterInput input) {
    return customers.register(input);
  }

  @GetMapping("/me")
  public LuanLHCE191099_UserPrincipal me(
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return user;
  }
}
