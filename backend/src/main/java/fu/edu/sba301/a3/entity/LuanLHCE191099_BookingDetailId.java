package fu.edu.sba301.a3.entity;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
@Embeddable
public class LuanLHCE191099_BookingDetailId implements Serializable {
    @Column(name="BookingReservationID") public Long reservationId;
    @Column(name="RoomID") public Long roomId;
    public LuanLHCE191099_BookingDetailId() {}
    public LuanLHCE191099_BookingDetailId(Long reservationId,Long roomId) {this.reservationId=reservationId;this.roomId=roomId;}
    @Override public boolean equals(Object value) {return value instanceof LuanLHCE191099_BookingDetailId other && Objects.equals(reservationId,other.reservationId) && Objects.equals(roomId,other.roomId);}
    @Override public int hashCode() {return Objects.hash(reservationId,roomId);}
}
