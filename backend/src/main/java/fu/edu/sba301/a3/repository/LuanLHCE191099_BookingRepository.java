package fu.edu.sba301.a3.repository;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Booking;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface LuanLHCE191099_BookingRepository extends JpaRepository<LuanLHCE191099_Booking,Long> {
    boolean existsByCustomer_Id(Long id);
    List<LuanLHCE191099_Booking> findByCustomer_IdOrderByIdDesc(Long id);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select b from LuanLHCE191099_Booking b where b.id=:id")
    Optional<LuanLHCE191099_Booking> findLockedById(@Param("id") Long id);
}
