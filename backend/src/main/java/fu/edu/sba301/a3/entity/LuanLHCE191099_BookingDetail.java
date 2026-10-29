package fu.edu.sba301.a3.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.math.BigDecimal;
@Entity @Table(name="BookingDetail")
public class LuanLHCE191099_BookingDetail {
    @EmbeddedId public LuanLHCE191099_BookingDetailId id;
    @MapsId("reservationId") @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="BookingReservationID",nullable=false) public LuanLHCE191099_Booking reservation;
    @MapsId("roomId") @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="RoomID",nullable=false) public LuanLHCE191099_Room room;
    @Column(name="StartDate",nullable=false) public LocalDate startDate;
    @Column(name="EndDate",nullable=false) public LocalDate endDate;
    @Column(name="ActualPrice",nullable=false,precision=12,scale=2) public BigDecimal actualPrice;
}
