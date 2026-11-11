package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
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
@Table(name = "RoomType")
public class LuanLHCE191099_RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoomTypeID")
    private Long id;

    @Nationalized
    @Column(name = "RoomTypeName", nullable = false, length = 100)
    private String name;

    @Nationalized
    @Column(name = "TypeDescription", length = 500)
    private String description;

    @Nationalized
    @Column(name = "TypeNote", length = 500)
    private String note;
}
