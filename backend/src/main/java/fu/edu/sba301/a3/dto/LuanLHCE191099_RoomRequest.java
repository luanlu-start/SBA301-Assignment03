package fu.edu.sba301.a3.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuanLHCE191099_RoomRequest {

    @NotBlank @Size(max = 20)
    private String number;

    @Size(max = 1000)
    private String description;

    @NotNull @Min(1) @Max(20)
    private Integer capacity;

    @NotNull @Positive
    private Long roomTypeId;

    @NotNull @Min(0) @Max(1)
    private Integer status;

    @NotNull @DecimalMin("0.01") @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
}
