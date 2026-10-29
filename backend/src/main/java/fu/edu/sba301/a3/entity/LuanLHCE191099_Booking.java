package fu.edu.sba301.a3.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
@Entity @Table(name="BookingReservation")
public class LuanLHCE191099_Booking {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="BookingReservationID") public Long id;
    @Column(name="BookingDate",nullable=false) public LocalDateTime bookingDate;
    @Column(name="TotalPrice",nullable=false,precision=16,scale=2) public BigDecimal totalPrice;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="CustomerID",nullable=false) public LuanLHCE191099_Customer customer;
    @Column(name="BookingStatus",nullable=false) public Integer status;
    @OneToMany(mappedBy="reservation",cascade=CascadeType.ALL,orphanRemoval=true)
    public List<LuanLHCE191099_BookingDetail> details=new ArrayList<>();
}
