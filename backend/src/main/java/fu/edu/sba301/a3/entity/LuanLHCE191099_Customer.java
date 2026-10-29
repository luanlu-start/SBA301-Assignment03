package fu.edu.sba301.a3.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import org.hibernate.annotations.Nationalized;
@Entity @Table(name="Customer")
public class LuanLHCE191099_Customer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) @Column(name="CustomerID") public Long id;
    @Nationalized @Column(name="CustomerFullName",nullable=false,length=100) public String fullName;
    @Column(name="Telephone",nullable=false,length=20) public String telephone;
    @Column(name="EmailAddress",nullable=false,unique=true,length=150) public String email;
    @Column(name="CustomerBirthday",nullable=false) public LocalDate birthday;
    @Column(name="CustomerStatus",nullable=false) public Integer status;
    @Column(name="Password",nullable=false,length=100) public String password;
}
