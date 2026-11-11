package fu.edu.sba301.a3.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
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
@Table(name = "Customer")
@JsonIncludeProperties({"id", "fullName", "telephone", "email", "birthday", "status"})
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
}
