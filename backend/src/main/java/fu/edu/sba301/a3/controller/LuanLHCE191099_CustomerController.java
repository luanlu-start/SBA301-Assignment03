package fu.edu.sba301.a3.controller;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.service.LuanLHCE191099_CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_CustomerController {

  private final LuanLHCE191099_CustomerService service;

  public LuanLHCE191099_CustomerController(LuanLHCE191099_CustomerService service) {
    this.service = service;
  }

  @GetMapping
  public List<CustomerResponse> list(@RequestParam(defaultValue = "") String q) {
    return service.list(q);
  }

  @GetMapping("/{id}")
  public CustomerResponse get(@PathVariable Long id) {
    return service.get(id);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CustomerResponse create(@Valid @RequestBody CustomerInput input) {
    return service.create(input);
  }

  @PutMapping("/{id}")
  public CustomerResponse update(@PathVariable Long id, @Valid @RequestBody CustomerInput input) {
    return service.update(id, input);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}
