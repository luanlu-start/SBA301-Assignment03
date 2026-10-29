package fu.edu.sba301.a3.entity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.Nationalized;
@Entity @Table(name="RoomInformation")
public class LuanLHCE191099_Room {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="RoomID") public Long id;
    @Column(name="RoomNumber",nullable=false,unique=true,length=20) public String number;
    @Nationalized @Column(name="RoomDetailDescription",length=1000) public String description;
    @Column(name="RoomMaxCapacity",nullable=false) public Integer capacity;
    @ManyToOne(fetch=FetchType.LAZY,optional=false) @JoinColumn(name="RoomTypeID",nullable=false) public LuanLHCE191099_RoomType type;
    @Column(name="RoomStatus",nullable=false) public Integer status;
    @Column(name="RoomPricePerDay",nullable=false,precision=12,scale=2) public BigDecimal price;
}
