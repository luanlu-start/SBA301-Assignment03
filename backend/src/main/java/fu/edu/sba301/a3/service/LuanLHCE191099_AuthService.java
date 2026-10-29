package fu.edu.sba301.a3.service;
import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import fu.edu.sba301.a3.repository.LuanLHCE191099_CustomerRepository;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.security.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @Transactional(readOnly=true)
public class LuanLHCE191099_AuthService {
    private final LuanLHCE191099_CustomerRepository customers;private final PasswordEncoder encoder;private final LuanLHCE191099_JwtService jwt;
    private final String staffEmail;private final String staffPassword;
    public LuanLHCE191099_AuthService(LuanLHCE191099_CustomerRepository customers,PasswordEncoder encoder,LuanLHCE191099_JwtService jwt,@Value("${app.staff.email}") String staffEmail,@Value("${app.staff.password}") String staffPassword) {
        this.customers=customers;this.encoder=encoder;this.jwt=jwt;this.staffEmail=staffEmail;this.staffPassword=staffPassword;
    }
    public AuthResponse login(LoginInput input) {
        if(input.email().trim().equalsIgnoreCase(staffEmail) && input.password().equals(staffPassword)) return new AuthResponse(jwt.issue("staff"),staff());
        LuanLHCE191099_Customer c=customers.findByEmailIgnoreCase(input.email().trim()).orElseThrow(() -> new LuanLHCE191099_ApiException(401,"Email or password is incorrect."));
        if(c.status!=1 || !encoder.matches(input.password(),c.password)) throw new LuanLHCE191099_ApiException(401,"Email or password is incorrect, or the account is inactive.");
        return new AuthResponse(jwt.issue("customer:"+c.id),principal(c));
    }
    public LuanLHCE191099_UserPrincipal loadSubject(String subject) {
        if(subject.equals("staff")) return staff();
        if(!subject.startsWith("customer:")) throw new LuanLHCE191099_ApiException(401,"Invalid login.");
        LuanLHCE191099_Customer c=customers.findById(Long.parseLong(subject.substring(9))).orElseThrow(() -> new LuanLHCE191099_ApiException(401,"Customer no longer exists."));
        if(c.status!=1) throw new LuanLHCE191099_ApiException(401,"Account is inactive.");return principal(c);
    }
    private LuanLHCE191099_UserPrincipal staff() {return new LuanLHCE191099_UserPrincipal(null,staffEmail,"Hotel staff","STAFF");}
    private LuanLHCE191099_UserPrincipal principal(LuanLHCE191099_Customer c) {return new LuanLHCE191099_UserPrincipal(c.id,c.email,c.fullName,"CUSTOMER");}
}
