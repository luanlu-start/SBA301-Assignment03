package fu.edu.sba301.a3.dto;
import fu.edu.sba301.a3.security.LuanLHCE191099_UserPrincipal;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;
public final class LuanLHCE191099_Dtos {
    private LuanLHCE191099_Dtos() {}
    public record LoginInput(@NotBlank @Email @Size(max=150) String email,@NotBlank @Size(max=100) String password) {}
    public record AuthResponse(String token,LuanLHCE191099_UserPrincipal user) {}
    public record RegisterInput(@NotBlank @Size(max=100) String fullName,@NotBlank @Pattern(regexp="[0-9+() .-]{7,20}") String telephone,
                                @NotBlank @Email @Size(max=150) String email,@NotNull @Past LocalDate birthday,@NotBlank @Size(min=6,max=100) String password) {}
    public record CustomerInput(@NotBlank @Size(max=100) String fullName,@NotBlank @Pattern(regexp="[0-9+() .-]{7,20}") String telephone,
                                @NotBlank @Email @Size(max=150) String email,@NotNull @Past LocalDate birthday,@Size(max=100) String password,@NotNull @Min(0) @Max(1) Integer status) {}
    public record ProfileInput(@NotBlank @Size(max=100) String fullName,@NotBlank @Pattern(regexp="[0-9+() .-]{7,20}") String telephone,
                               @NotBlank @Email @Size(max=150) String email,@NotNull @Past LocalDate birthday,@Size(max=100) String password) {}
    public record CustomerResponse(Long id,String fullName,String telephone,String email,LocalDate birthday,Integer status) {}
    public record RoomTypeResponse(Long id,String name,String description,String note) {}
    public record RoomInput(@NotBlank @Size(max=20) String number,@Size(max=1000) String description,@NotNull @Min(1) @Max(20) Integer capacity,
                            @NotNull @Positive Long roomTypeId,@NotNull @Min(0) @Max(1) Integer status,@NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) BigDecimal price) {}
    public record RoomResponse(Long id,String number,String description,Integer capacity,Long roomTypeId,String roomTypeName,Integer status,BigDecimal price) {}
    public record BookingInput(@Positive Long customerId,@NotNull LocalDate startDate,@NotNull LocalDate endDate,
                               @NotEmpty @Size(max=20) List<@NotNull @Positive Long> roomIds,@NotNull @Min(0) @Max(1) Integer status) {}
    public record DetailResponse(Long roomId,String roomNumber,LocalDate startDate,LocalDate endDate,BigDecimal actualPrice,BigDecimal subtotal) {}
    public record BookingResponse(Long id,LocalDateTime bookingDate,BigDecimal totalPrice,Long customerId,String customerName,String customerEmail,Integer status,List<DetailResponse> details) {}
}
