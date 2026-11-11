package fu.edu.sba301.a3.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "RoomInformation")
@JsonIncludeProperties({"id", "number", "description", "capacity", "roomTypeId", "roomTypeName", "status", "price"})
public class LuanLHCE191099_Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomID")
    private Long id;

    @Column(name = "RoomNumber", nullable = false, unique = true, length = 20)
    private String number;

    @Nationalized
    @Column(name = "RoomDetailDescription", length = 1000)
    private String description;

    @Column(name = "RoomMaxCapacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "RoomTypeID", nullable = false)
    private LuanLHCE191099_RoomType type;

    @Column(name = "RoomStatus", nullable = false)
    private Integer status;

    @Column(name = "RoomPricePerDay", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    public Long getRoomTypeId() {
        return type.getId();
    }

    public String getRoomTypeName() {
        return type.getName();
    }
}
