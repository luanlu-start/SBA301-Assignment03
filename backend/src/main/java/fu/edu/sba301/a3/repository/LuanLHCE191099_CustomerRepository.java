package fu.edu.sba301.a3.repository;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface LuanLHCE191099_CustomerRepository extends JpaRepository<LuanLHCE191099_Customer,Long> {Optional<LuanLHCE191099_Customer> findByEmailIgnoreCase(String email);}
