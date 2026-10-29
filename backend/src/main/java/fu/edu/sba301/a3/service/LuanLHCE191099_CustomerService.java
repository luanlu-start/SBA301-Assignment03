package fu.edu.sba301.a3.service;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.*;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_CustomerService {

  private final LuanLHCE191099_CustomerRepository customers;
  private final LuanLHCE191099_BookingRepository bookings;
  private final PasswordEncoder encoder;
  private final String staffEmail;

  public LuanLHCE191099_CustomerService(
    LuanLHCE191099_CustomerRepository customers,
    LuanLHCE191099_BookingRepository bookings,
    PasswordEncoder encoder,
    @Value("${app.staff.email}") String staffEmail
  ) {
    this.customers = customers;
    this.bookings = bookings;
    this.encoder = encoder;
    this.staffEmail = staffEmail;
  }

  public List<CustomerResponse> list(String q) {
    String k = q.trim().toLowerCase(Locale.ROOT);
    return customers
      .findAll(Sort.by("id"))
      .stream()
      .filter(c ->
        (c.getFullName() + " " + c.getEmail() + " " + c.getTelephone())
          .toLowerCase(Locale.ROOT)
          .contains(k)
      )
      .map(LuanLHCE191099_CustomerService::view)
      .toList();
  }

  public CustomerResponse get(Long id) {
    return view(required(id));
  }

  @Transactional
  public CustomerResponse create(CustomerInput input) {
    LuanLHCE191099_Customer c = new LuanLHCE191099_Customer();
    apply(c, input);
    return view(customers.saveAndFlush(c));
  }

  @Transactional
  public CustomerResponse update(Long id, CustomerInput input) {
    LuanLHCE191099_Customer c = required(id);
    apply(c, input);
    return view(customers.saveAndFlush(c));
  }

  @Transactional
  public CustomerResponse register(RegisterInput input) {
    return create(
      new CustomerInput(
        input.fullName(),
        input.telephone(),
        input.email(),
        input.birthday(),
        input.password(),
        1
      )
    );
  }

  @Transactional
  public CustomerResponse profile(Long id, ProfileInput input) {
    LuanLHCE191099_Customer c = required(id);
    apply(
      c,
      new CustomerInput(
        input.fullName(),
        input.telephone(),
        input.email(),
        input.birthday(),
        input.password(),
        c.getStatus()
      )
    );
    return view(customers.saveAndFlush(c));
  }

  @Transactional
  public void delete(Long id) {
    LuanLHCE191099_Customer c = required(id);
    if (bookings.existsByCustomer_Id(id)) throw new LuanLHCE191099_ApiException(
      409,
      "This customer has booking history. Set the account inactive instead."
    );
    customers.delete(c);
    customers.flush();
  }

  private void apply(LuanLHCE191099_Customer c, CustomerInput input) {
    String email = input.email().trim().toLowerCase(Locale.ROOT);
    if (email.equalsIgnoreCase(staffEmail)) throw new LuanLHCE191099_ApiException(
      409,
      "This email is reserved for Staff."
    );
    customers
      .findByEmailIgnoreCase(email)
      .filter(other -> !other.getId().equals(c.getId()))
      .ifPresent(other -> {
        throw new LuanLHCE191099_ApiException(409, "Email already exists.");
      });
    if (
      c.getId() == null && (input.password() == null || input.password().isBlank())
    ) throw new LuanLHCE191099_ApiException(400, "Password is required.");
    if (input.password() != null && !input.password().isBlank()) {
      if (input.password().length() < 6) throw new LuanLHCE191099_ApiException(
        400,
        "Password must contain at least 6 characters."
      );
      c.setPassword(encoder.encode(input.password()));
    }
    c.setFullName(input.fullName().trim());
    c.setTelephone(input.telephone().trim());
    c.setEmail(email);
    c.setBirthday(input.birthday());
    c.setStatus(input.status());
  }

  private LuanLHCE191099_Customer required(Long id) {
    return customers
      .findById(id)
      .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Customer not found."));
  }

  public static CustomerResponse view(LuanLHCE191099_Customer c) {
    return new CustomerResponse(
      c.getId(),
      c.getFullName(),
      c.getTelephone(),
      c.getEmail(),
      c.getBirthday(),
      c.getStatus()
    );
  }
}
