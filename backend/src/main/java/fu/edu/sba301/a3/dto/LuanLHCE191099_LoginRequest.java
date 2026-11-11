package fu.edu.sba301.a3.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuanLHCE191099_LoginRequest {

    @NotBlank @Email @Size(max = 150)
    private String email;

    @ToString.Exclude
    @NotBlank @Size(max = 100)
    private String password;
}
