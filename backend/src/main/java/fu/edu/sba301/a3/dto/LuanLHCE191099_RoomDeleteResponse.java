package fu.edu.sba301.a3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuanLHCE191099_RoomDeleteResponse {

    private String message;
    private boolean deactivated;
}
