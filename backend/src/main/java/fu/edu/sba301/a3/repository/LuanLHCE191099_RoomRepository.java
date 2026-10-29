package fu.edu.sba301.a3.repository;

import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface LuanLHCE191099_RoomRepository extends JpaRepository<LuanLHCE191099_Room, Long> {
  Optional<LuanLHCE191099_Room> findByNumber(String number);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select r from LuanLHCE191099_Room r where r.id=:id")
  Optional<LuanLHCE191099_Room> findLockedById(@Param("id") Long id);
}
