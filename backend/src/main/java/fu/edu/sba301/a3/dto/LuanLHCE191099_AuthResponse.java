package fu.edu.sba301.a3.dto;

import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuanLHCE191099_AuthResponse {

    @ToString.Exclude
    private String token;

    private LuanLHCE191099_UserPrincipal user;
}
