package fu.edu.sba301.a3.repository;

import fu.edu.sba301.a3.entity.*;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface LuanLHCE191099_BookingDetailRepository
  extends JpaRepository<LuanLHCE191099_BookingDetail, LuanLHCE191099_BookingDetailId>
{
  boolean existsByRoom_Id(Long id);

  @Query(
    "select count(d) from LuanLHCE191099_BookingDetail d where d.room.id=:roomId and d.reservation.status=1 and d.startDate < :endDate and d.endDate > :startDate and (:excludeId is null or d.reservation.id <> :excludeId)"
  )
  long countOverlap(
    @Param("roomId") Long roomId,
    @Param("startDate") LocalDate startDate,
    @Param("endDate") LocalDate endDate,
    @Param("excludeId") Long excludeId
  );
}
