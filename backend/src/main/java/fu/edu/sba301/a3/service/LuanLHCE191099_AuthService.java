package fu.edu.sba301.a3.service;

import fu.edu.sba301.a3.dto.LuanLHCE191099_AuthResponse;
import fu.edu.sba301.a3.dto.LuanLHCE191099_LoginRequest;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.LuanLHCE191099_CustomerRepository;
import fu.edu.sba301.a3.security.LuanLHCE191099_JwtService;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class LuanLHCE191099_AuthService {

    private static final String STAFF_SUBJECT = "staff";
    private static final String CUSTOMER_SUBJECT_PREFIX = "customer:";

    private final LuanLHCE191099_CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final LuanLHCE191099_JwtService jwtService;
    private final String staffEmail;
    private final String staffPassword;

    public LuanLHCE191099_AuthService(
        LuanLHCE191099_CustomerRepository customerRepository,
        PasswordEncoder passwordEncoder,
        LuanLHCE191099_JwtService jwtService,
        @Value("${app.staff.email}") String staffEmail,
        @Value("${app.staff.password}") String staffPassword
    ) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.staffEmail = staffEmail;
        this.staffPassword = staffPassword;
    }

    public LuanLHCE191099_AuthResponse login(LuanLHCE191099_LoginRequest request) {
        String email = request.getEmail().trim();
        String password = request.getPassword();

        if (email.equalsIgnoreCase(staffEmail) && password.equals(staffPassword)) {
            return LuanLHCE191099_AuthResponse.builder()
                .token(jwtService.issue(STAFF_SUBJECT))
                .user(staffPrincipal())
                .build();
        }

        LuanLHCE191099_Customer customer = customerRepository.findByEmailIgnoreCase(email)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(401, "Email or password is incorrect."));

        if (customer.getStatus() != 1 || !passwordEncoder.matches(password, customer.getPassword())) {
            throw new LuanLHCE191099_ApiException(
                401, "Email or password is incorrect, or the account is inactive."
            );
        }

        return LuanLHCE191099_AuthResponse.builder()
            .token(jwtService.issue(CUSTOMER_SUBJECT_PREFIX + customer.getId()))
            .user(toCustomerPrincipal(customer))
            .build();
    }

    public LuanLHCE191099_UserPrincipal loadSubject(String subject) {
        if (subject.equals(STAFF_SUBJECT)) {
            return staffPrincipal();
        }
        if (!subject.startsWith(CUSTOMER_SUBJECT_PREFIX)) {
            throw new LuanLHCE191099_ApiException(401, "Invalid login.");
        }

        Long customerId = Long.valueOf(subject.substring(CUSTOMER_SUBJECT_PREFIX.length()));
        LuanLHCE191099_Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new LuanLHCE191099_ApiException(401, "Customer no longer exists."));
        if (customer.getStatus() != 1) {
            throw new LuanLHCE191099_ApiException(401, "Account is inactive.");
        }
        return toCustomerPrincipal(customer);
    }

    private LuanLHCE191099_UserPrincipal staffPrincipal() {
        return LuanLHCE191099_UserPrincipal.builder()
            .email(staffEmail)
            .name("Hotel staff")
            .role("STAFF")
            .build();
    }

    private LuanLHCE191099_UserPrincipal toCustomerPrincipal(LuanLHCE191099_Customer customer) {
        return LuanLHCE191099_UserPrincipal.builder()
            .id(customer.getId())
            .email(customer.getEmail())
            .name(customer.getFullName())
            .role("CUSTOMER")
            .build();
    }
}
