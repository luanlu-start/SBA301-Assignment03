package fu.edu.sba301.a3.repository;

import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface LuanLHCE191099_RoomRepository extends JpaRepository<LuanLHCE191099_Room, Long> {
    @Override
    @EntityGraph(attributePaths = "type")
    List<LuanLHCE191099_Room> findAll(Sort sort);

    @Override
    @EntityGraph(attributePaths = "type")
    Optional<LuanLHCE191099_Room> findById(Long id);

    Optional<LuanLHCE191099_Room> findByNumber(String number);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<LuanLHCE191099_Room> findLockedById(Long id);
}
