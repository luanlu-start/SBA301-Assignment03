package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BookingReservation")
public class LuanLHCE191099_Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "BookingReservationID")
  private Long id;

  @Column(name = "BookingDate", nullable = false)
  private LocalDateTime bookingDate;

  @Column(name = "TotalPrice", nullable = false, precision = 16, scale = 2)
  private BigDecimal totalPrice;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "CustomerID", nullable = false)
  private LuanLHCE191099_Customer customer;

  @Column(name = "BookingStatus", nullable = false)
  private Integer status;

  @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<LuanLHCE191099_BookingDetail> details = new ArrayList<>();

  public Long getId() {
    return this.id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public LocalDateTime getBookingDate() {
    return this.bookingDate;
  }

  public void setBookingDate(LocalDateTime value) {
    this.bookingDate = value;
  }

  public BigDecimal getTotalPrice() {
    return this.totalPrice;
  }

  public void setTotalPrice(BigDecimal value) {
    this.totalPrice = value;
  }

  public LuanLHCE191099_Customer getCustomer() {
    return this.customer;
  }

  public void setCustomer(LuanLHCE191099_Customer value) {
    this.customer = value;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer value) {
    this.status = value;
  }

  public List<LuanLHCE191099_BookingDetail> getDetails() {
    return this.details;
  }

  public void setDetails(List<LuanLHCE191099_BookingDetail> value) {
    this.details = value;
  }
}
