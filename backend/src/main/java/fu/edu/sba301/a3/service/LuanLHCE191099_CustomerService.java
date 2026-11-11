package fu.edu.sba301.a3.service;

import fu.edu.sba301.a3.dto.LuanLHCE191099_CustomerRequest;
import fu.edu.sba301.a3.dto.LuanLHCE191099_ProfileRequest;
import fu.edu.sba301.a3.dto.LuanLHCE191099_RegisterRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.LuanLHCE191099_BookingRepository;
import fu.edu.sba301.a3.repository.LuanLHCE191099_CustomerRepository;
import java.util.ArrayList;
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

    private final LuanLHCE191099_CustomerRepository customerRepository;
    private final LuanLHCE191099_BookingRepository bookingRepository;
    private final PasswordEncoder passwordEncoder;
    private final String staffEmail;

    public LuanLHCE191099_CustomerService(
        LuanLHCE191099_CustomerRepository customerRepository,
        LuanLHCE191099_BookingRepository bookingRepository,
        PasswordEncoder passwordEncoder,
        @Value("${app.staff.email}") String staffEmail
    ) {
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffEmail = staffEmail;
    }

    public List<LuanLHCE191099_Customer> getCustomers(String searchText) {
        String keyword = searchText.trim().toLowerCase(Locale.ROOT);
        List<LuanLHCE191099_Customer> customers = customerRepository.findAll(Sort.by("id"));
        List<LuanLHCE191099_Customer> matchingCustomers = new ArrayList<>();

        for (LuanLHCE191099_Customer customer : customers) {
            String customerText = customer.getFullName() + " " + customer.getEmail() + " " + customer.getTelephone();
            if (customerText.toLowerCase(Locale.ROOT).contains(keyword)) {
                matchingCustomers.add(customer);
            }
        }
        return matchingCustomers;
    }

    public LuanLHCE191099_Customer getCustomerById(Long id) {
        return findCustomerById(id);
    }

    @Transactional
    public LuanLHCE191099_Customer createCustomer(LuanLHCE191099_CustomerRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        validateEmail(email, null);
        String password = request.getPassword();
        if (password == null || password.isBlank()) {
            throw new LuanLHCE191099_ApiException(400, "Password is required.");
        }

        LuanLHCE191099_Customer customer = LuanLHCE191099_Customer.builder()
            .fullName(request.getFullName().trim())
            .telephone(request.getTelephone().trim())
            .email(email)
            .birthday(request.getBirthday())
            .status(request.getStatus())
            .password(encodePassword(password))
            .build();

        return customerRepository.save(customer);
    }

    @Transactional
    public LuanLHCE191099_Customer updateCustomer(Long id, LuanLHCE191099_CustomerRequest request) {
        LuanLHCE191099_Customer customer = findCustomerById(id);
        applyCustomerRequest(customer, request);

        return customerRepository.save(customer);
    }

    @Transactional
    public LuanLHCE191099_Customer registerCustomer(LuanLHCE191099_RegisterRequest request) {
        LuanLHCE191099_CustomerRequest customerRequest = LuanLHCE191099_CustomerRequest.builder()
            .fullName(request.getFullName())
            .telephone(request.getTelephone())
            .email(request.getEmail())
            .birthday(request.getBirthday())
            .password(request.getPassword())
            .status(1)
            .build();

        return createCustomer(customerRequest);
    }

    @Transactional
    public LuanLHCE191099_Customer updateProfile(Long id, LuanLHCE191099_ProfileRequest request) {
        LuanLHCE191099_Customer customer = findCustomerById(id);
        LuanLHCE191099_CustomerRequest customerRequest = LuanLHCE191099_CustomerRequest.builder()
            .fullName(request.getFullName())
            .telephone(request.getTelephone())
            .email(request.getEmail())
            .birthday(request.getBirthday())
            .password(request.getPassword())
            .status(customer.getStatus())
            .build();

        applyCustomerRequest(customer, customerRequest);
        return customerRepository.save(customer);
    }

    @Transactional
    public void deleteCustomer(Long id) {
        LuanLHCE191099_Customer customer = findCustomerById(id);
        if (bookingRepository.existsByCustomer_Id(id)) {
            throw new LuanLHCE191099_ApiException(
                409, "This customer has booking history. Set the account inactive instead."
            );
        }
        customerRepository.delete(customer);
    }

    private void applyCustomerRequest(
        LuanLHCE191099_Customer customer,
        LuanLHCE191099_CustomerRequest request
    ) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        validateEmail(email, customer.getId());

        String password = request.getPassword();
        if (password != null && !password.isBlank()) {
            customer.setPassword(encodePassword(password));
        }

        customer.setFullName(request.getFullName().trim());
        customer.setTelephone(request.getTelephone().trim());
        customer.setEmail(email);
        customer.setBirthday(request.getBirthday());
        customer.setStatus(request.getStatus());
    }

    private void validateEmail(String email, Long customerId) {
        if (email.equalsIgnoreCase(staffEmail)) {
            throw new LuanLHCE191099_ApiException(409, "This email is reserved for Staff.");
        }

        boolean emailUsedByAnotherCustomer = customerRepository.findByEmailIgnoreCase(email)
            .filter(other -> !other.getId().equals(customerId))
            .isPresent();
        if (emailUsedByAnotherCustomer) {
            throw new LuanLHCE191099_ApiException(409, "Email already exists.");
        }
    }

    private String encodePassword(String password) {
        if (password.length() < 6) {
            throw new LuanLHCE191099_ApiException(400, "Password must contain at least 6 characters.");
        }
        return passwordEncoder.encode(password);
    }

    private LuanLHCE191099_Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(404, "Customer not found."));
    }
}
