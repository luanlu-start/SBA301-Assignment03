package fu.edu.sba301.a3.controller;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a3.service.LuanLHCE191099_BookingService;
import fu.edu.sba301.a3.service.LuanLHCE191099_CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/my")
@PreAuthorize("hasRole('CUSTOMER')")
public class LuanLHCE191099_MyController {

  private final LuanLHCE191099_CustomerService customers;
  private final LuanLHCE191099_BookingService bookings;

  public LuanLHCE191099_MyController(
    LuanLHCE191099_CustomerService customers,
    LuanLHCE191099_BookingService bookings
  ) {
    this.customers = customers;
    this.bookings = bookings;
  }

  @GetMapping("/profile")
  public CustomerResponse profile(@AuthenticationPrincipal LuanLHCE191099_UserPrincipal user) {
    return customers.get(user.id());
  }

  @PutMapping("/profile")
  public CustomerResponse profile(
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user,
    @Valid @RequestBody ProfileInput input
  ) {
    return customers.profile(user.id(), input);
  }

  @GetMapping("/bookings")
  public List<BookingResponse> history(
    @RequestParam(defaultValue = "") String q,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return bookings.list(q, user.id());
  }
}
