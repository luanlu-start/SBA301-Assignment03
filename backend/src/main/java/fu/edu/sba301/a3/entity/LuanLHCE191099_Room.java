package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "RoomInformation")
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

  public Long getId() {
    return this.id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getNumber() {
    return this.number;
  }

  public void setNumber(String value) {
    this.number = value;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String value) {
    this.description = value;
  }

  public Integer getCapacity() {
    return this.capacity;
  }

  public void setCapacity(Integer value) {
    this.capacity = value;
  }

  public LuanLHCE191099_RoomType getType() {
    return this.type;
  }

  public void setType(LuanLHCE191099_RoomType value) {
    this.type = value;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer value) {
    this.status = value;
  }

  public BigDecimal getPrice() {
    return this.price;
  }

  public void setPrice(BigDecimal value) {
    this.price = value;
  }
}
