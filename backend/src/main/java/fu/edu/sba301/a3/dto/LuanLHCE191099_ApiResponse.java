package fu.edu.sba301.a3.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuanLHCE191099_ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public static <T> LuanLHCE191099_ApiResponse<T> ok(T data) {
        return ok("Success", data);
    }

    public static <T> LuanLHCE191099_ApiResponse<T> ok(String message, T data) {
        return LuanLHCE191099_ApiResponse.<T>builder()
            .success(true)
            .message(message)
            .data(data)
            .build();
    }

    public static LuanLHCE191099_ApiResponse<Void> error(String message) {
        return error(message, null);
    }

    public static <T> LuanLHCE191099_ApiResponse<T> error(String message, T data) {
        return LuanLHCE191099_ApiResponse.<T>builder()
            .success(false)
            .message(message)
            .data(data)
            .build();
    }
}
