package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class LuanLHCE191099_BookingDetailId implements Serializable {

  @Column(name = "BookingReservationID")
  private Long reservationId;

  @Column(name = "RoomID")
  private Long roomId;

  public LuanLHCE191099_BookingDetailId() {}

  public LuanLHCE191099_BookingDetailId(Long reservationId, Long roomId) {
    this.setReservationId(reservationId);
    this.setRoomId(roomId);
  }

  @Override
  public boolean equals(Object value) {
    return (
      value instanceof LuanLHCE191099_BookingDetailId other &&
      Objects.equals(reservationId, other.getReservationId()) &&
      Objects.equals(roomId, other.getRoomId())
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(reservationId, roomId);
  }

  public Long getReservationId() {
    return this.reservationId;
  }

  public void setReservationId(Long value) {
    this.reservationId = value;
  }

  public Long getRoomId() {
    return this.roomId;
  }

  public void setRoomId(Long value) {
    this.roomId = value;
  }
}
