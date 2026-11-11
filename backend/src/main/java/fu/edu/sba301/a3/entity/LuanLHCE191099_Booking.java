package fu.edu.sba301.a3.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "BookingReservation")
@JsonIncludeProperties({"id", "bookingDate", "totalPrice", "customerId", "customerName", "customerEmail", "status", "details"})
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

    @Builder.Default
    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LuanLHCE191099_BookingDetail> details = new ArrayList<>();

    public Long getCustomerId() {
        return customer.getId();
    }

    public String getCustomerName() {
        return customer.getFullName();
    }

    public String getCustomerEmail() {
        return customer.getEmail();
    }
}
