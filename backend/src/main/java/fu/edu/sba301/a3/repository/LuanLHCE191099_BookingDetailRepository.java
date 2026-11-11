package fu.edu.sba301.a3.repository;

import fu.edu.sba301.a3.entity.LuanLHCE191099_BookingDetail;
import fu.edu.sba301.a3.entity.LuanLHCE191099_BookingDetailId;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LuanLHCE191099_BookingDetailRepository
    extends JpaRepository<LuanLHCE191099_BookingDetail, LuanLHCE191099_BookingDetailId>
{
    boolean existsByRoom_Id(Long id);

    boolean existsByRoom_IdAndReservation_StatusAndStartDateBeforeAndEndDateAfter(
        Long roomId,
        Integer reservationStatus,
        LocalDate endDate,
        LocalDate startDate
    );

    boolean existsByRoom_IdAndReservation_StatusAndStartDateBeforeAndEndDateAfterAndReservation_IdNot(
        Long roomId,
        Integer reservationStatus,
        LocalDate endDate,
        LocalDate startDate,
        Long excludedReservationId
    );

    default boolean hasOverlap(
        Long roomId,
        LocalDate startDate,
        LocalDate endDate,
        Long excludedReservationId
    ) {
        int confirmedStatus = 1;
        if (excludedReservationId == null) {
            return existsByRoom_IdAndReservation_StatusAndStartDateBeforeAndEndDateAfter(
                roomId,
                confirmedStatus,
                endDate,
                startDate
            );
        }
        return existsByRoom_IdAndReservation_StatusAndStartDateBeforeAndEndDateAfterAndReservation_IdNot(
            roomId,
            confirmedStatus,
            endDate,
            startDate,
            excludedReservationId
        );
    }
}
