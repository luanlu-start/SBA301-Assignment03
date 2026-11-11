package fu.edu.sba301.a3.exception;

import fu.edu.sba301.a3.dto.LuanLHCE191099_ApiResponse;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class LuanLHCE191099_ApiExceptionHandler {

    @ExceptionHandler(LuanLHCE191099_ApiException.class)
    ResponseEntity<LuanLHCE191099_ApiResponse<Void>> business(LuanLHCE191099_ApiException ex) {
        return ResponseEntity.status(ex.status).body(LuanLHCE191099_ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<LuanLHCE191099_ApiResponse<Map<String, String>>> validation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult()
            .getFieldErrors()
            .forEach(e -> errors.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(
            LuanLHCE191099_ApiResponse.error("Please check the entered information.", errors)
        );
    }

    @ExceptionHandler({
        HttpMessageNotReadableException.class,
        MethodArgumentTypeMismatchException.class,
    })
    ResponseEntity<LuanLHCE191099_ApiResponse<Void>> badInput(Exception ex) {
        return ResponseEntity.badRequest().body(
            LuanLHCE191099_ApiResponse.error("Invalid request data or field type.")
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<LuanLHCE191099_ApiResponse<Void>> constraint(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(
            LuanLHCE191099_ApiResponse.error("The record conflicts with existing or related data.")
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<LuanLHCE191099_ApiResponse<Void>> forbidden(AccessDeniedException ex) {
        return ResponseEntity.status(403).body(
            LuanLHCE191099_ApiResponse.error("You do not have permission for this operation.")
        );
    }
}
