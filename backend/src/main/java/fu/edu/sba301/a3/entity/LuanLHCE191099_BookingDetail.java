package fu.edu.sba301.a3.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "BookingDetail")
@JsonIncludeProperties({"roomId", "roomNumber", "startDate", "endDate", "actualPrice", "subtotal"})
public class LuanLHCE191099_BookingDetail {

    @EmbeddedId
    private LuanLHCE191099_BookingDetailId id;

    @MapsId("reservationId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BookingReservationID", nullable = false)
    private LuanLHCE191099_Booking reservation;

    @MapsId("roomId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomID", nullable = false)
    private LuanLHCE191099_Room room;

    @Column(name = "StartDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "EndDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "ActualPrice", nullable = false, precision = 12, scale = 2)
    private BigDecimal actualPrice;

    public Long getRoomId() {
        return room.getId();
    }

    public String getRoomNumber() {
        return room.getNumber();
    }

    public BigDecimal getSubtotal() {
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        return actualPrice.multiply(BigDecimal.valueOf(nights));
    }
}
