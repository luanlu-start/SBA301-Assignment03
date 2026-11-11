package fu.edu.sba301.a3.controller;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_CustomerRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.service.LuanLHCE191099_CustomerService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
@PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_CustomerController {

    private final LuanLHCE191099_CustomerService customerService;

    @GetMapping
    public ResponseEntity<LuanLHCE191099_ApiResponse<List<LuanLHCE191099_Customer>>> getCustomers(
        @RequestParam(name = "q", defaultValue = "") String searchText
    ) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(customerService.getCustomers(searchText)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Customer>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok(customerService.getCustomerById(id)));
    }

    @PostMapping
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Customer>> createCustomer(
        @Valid @RequestBody LuanLHCE191099_CustomerRequest request
    ) {
        LuanLHCE191099_Customer customer = customerService.createCustomer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(LuanLHCE191099_ApiResponse.ok("Customer created successfully.", customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LuanLHCE191099_ApiResponse<LuanLHCE191099_Customer>> updateCustomer(
        @PathVariable Long id,
        @Valid @RequestBody LuanLHCE191099_CustomerRequest request
    ) {
        LuanLHCE191099_Customer customer = customerService.updateCustomer(id, request);
        return ResponseEntity.ok(LuanLHCE191099_ApiResponse.ok("Customer updated successfully.", customer));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
    }
}
