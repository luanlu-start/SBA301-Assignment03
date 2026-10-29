package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BookingDetail")
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

  public LuanLHCE191099_BookingDetailId getId() {
    return this.id;
  }

  public void setId(LuanLHCE191099_BookingDetailId value) {
    this.id = value;
  }

  public LuanLHCE191099_Booking getReservation() {
    return this.reservation;
  }

  public void setReservation(LuanLHCE191099_Booking value) {
    this.reservation = value;
  }

  public LuanLHCE191099_Room getRoom() {
    return this.room;
  }

  public void setRoom(LuanLHCE191099_Room value) {
    this.room = value;
  }

  public LocalDate getStartDate() {
    return this.startDate;
  }

  public void setStartDate(LocalDate value) {
    this.startDate = value;
  }

  public LocalDate getEndDate() {
    return this.endDate;
  }

  public void setEndDate(LocalDate value) {
    this.endDate = value;
  }

  public BigDecimal getActualPrice() {
    return this.actualPrice;
  }

  public void setActualPrice(BigDecimal value) {
    this.actualPrice = value;
  }
}
