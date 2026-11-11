package fu.edu.sba301.a3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuanLHCE191099_ProfileRequest {

    @NotBlank @Size(max = 100)
    private String fullName;

    @NotBlank @Pattern(regexp = "[0-9+() .-]{7,20}")
    private String telephone;

    @NotBlank @Email @Size(max = 150)
    private String email;

    @NotNull @Past
    private LocalDate birthday;

    @ToString.Exclude
    @Size(max = 100)
    private String password;
}
