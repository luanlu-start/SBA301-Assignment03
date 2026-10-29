package fu.edu.sba301.a3.controller;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import fu.edu.sba301.a3.service.LuanLHCE191099_BookingService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class LuanLHCE191099_BookingController {

  private final LuanLHCE191099_BookingService service;

  public LuanLHCE191099_BookingController(LuanLHCE191099_BookingService service) {
    this.service = service;
  }

  @GetMapping
  @PreAuthorize("hasRole('STAFF')")
  public List<BookingResponse> list(@RequestParam(defaultValue = "") String q) {
    return service.list(q, null);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAnyRole('STAFF','CUSTOMER')")
  public BookingResponse get(
    @PathVariable Long id,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return service.get(id, user.role().equals("CUSTOMER") ? user.id() : null);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasAnyRole('STAFF','CUSTOMER')")
  public BookingResponse create(
    @Valid @RequestBody BookingInput input,
    @AuthenticationPrincipal LuanLHCE191099_UserPrincipal user
  ) {
    return service.create(input, user);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('STAFF')")
  public BookingResponse update(@PathVariable Long id, @Valid @RequestBody BookingInput input) {
    return service.update(id, input);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize("hasRole('STAFF')")
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
