package fu.edu.sba301.a3.entity;
import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;
@Entity @Table(name="RoomType")
public class LuanLHCE191099_RoomType {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="RoomTypeID") public Long id;
    @Nationalized @Column(name="RoomTypeName",nullable=false,length=100) public String name;
    @Nationalized @Column(name="TypeDescription",length=500) public String description;
    @Nationalized @Column(name="TypeNote",length=500) public String note;
}
