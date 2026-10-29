package fu.edu.sba301.a3.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.Nationalized;

@Entity
@Table(name = "Customer")
public class LuanLHCE191099_Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "CustomerID")
  private Long id;

  @Nationalized
  @Column(name = "CustomerFullName", nullable = false, length = 100)
  private String fullName;

  @Column(name = "Telephone", nullable = false, length = 20)
  private String telephone;

  @Column(name = "EmailAddress", nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "CustomerBirthday", nullable = false)
  private LocalDate birthday;

  @Column(name = "CustomerStatus", nullable = false)
  private Integer status;

  @Column(name = "Password", nullable = false, length = 100)
  private String password;

  public Long getId() {
    return this.id;
  }

  public void setId(Long value) {
    this.id = value;
  }

  public String getFullName() {
    return this.fullName;
  }

  public void setFullName(String value) {
    this.fullName = value;
  }

  public String getTelephone() {
    return this.telephone;
  }

  public void setTelephone(String value) {
    this.telephone = value;
  }

  public String getEmail() {
    return this.email;
  }

  public void setEmail(String value) {
    this.email = value;
  }

  public LocalDate getBirthday() {
    return this.birthday;
  }

  public void setBirthday(LocalDate value) {
    this.birthday = value;
  }

  public Integer getStatus() {
    return this.status;
  }

  public void setStatus(Integer value) {
    this.status = value;
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String value) {
    this.password = value;
  }
}
