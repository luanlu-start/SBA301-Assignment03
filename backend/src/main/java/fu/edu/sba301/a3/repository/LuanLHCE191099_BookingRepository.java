package fu.edu.sba301.a3.repository;

import fu.edu.sba301.a3.entity.LuanLHCE191099_Booking;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface LuanLHCE191099_BookingRepository
    extends JpaRepository<LuanLHCE191099_Booking, Long>
{
    @Override
    @EntityGraph(attributePaths = {"customer", "details", "details.room"})
    List<LuanLHCE191099_Booking> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = {"customer", "details", "details.room"})
    Optional<LuanLHCE191099_Booking> findById(Long id);

    boolean existsByCustomer_Id(Long id);

    @EntityGraph(attributePaths = {"customer", "details", "details.room"})
    List<LuanLHCE191099_Booking> findByCustomer_IdOrderByIdDesc(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LuanLHCE191099_Booking> findLockedById(Long id);
}
