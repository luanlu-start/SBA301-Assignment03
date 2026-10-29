package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Nationalized;

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

  public Long getId() {
    return this.id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getName() {
    return this.name;
  }

  public void setName(String value) {
    this.name = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public String getNote() {
    return this.note;
  }

  public void setNote(String value) {
    this.note = value;
  }
}
