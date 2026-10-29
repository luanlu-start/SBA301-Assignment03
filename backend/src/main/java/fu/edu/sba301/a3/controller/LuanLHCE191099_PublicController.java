package fu.edu.sba301.a3.controller;

import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;

import fu.edu.sba301.a3.service.LuanLHCE191099_RoomService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class LuanLHCE191099_PublicController {

  private final LuanLHCE191099_RoomService service;

  public LuanLHCE191099_PublicController(LuanLHCE191099_RoomService service) {
    this.service = service;
  }

  @GetMapping("/rooms")
  public List<RoomResponse> rooms(
    @RequestParam(defaultValue = "") String q,
    @RequestParam(required = false) @DateTimeFormat(
      iso = DateTimeFormat.ISO.DATE
    ) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
  ) {
    return service.list(q, true, startDate, endDate);
  }

  @GetMapping("/rooms/{id}")
  public RoomResponse room(@PathVariable Long id) {
    return service.get(id, true);
  }

  @GetMapping("/room-types")
  public List<RoomTypeResponse> types() {
    return service.types();
  }
}
