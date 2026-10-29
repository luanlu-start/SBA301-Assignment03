package fu.edu.sba301.a3.controller;
import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;
import fu.edu.sba301.a3.service.LuanLHCE191099_RoomService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/rooms") @PreAuthorize("hasRole('STAFF')")
public class LuanLHCE191099_RoomController {
    private final LuanLHCE191099_RoomService service;
    public LuanLHCE191099_RoomController(LuanLHCE191099_RoomService service) {this.service=service;}
    @GetMapping public List<RoomResponse> list(@RequestParam(defaultValue="") String q) {return service.list(q,false,null,null);}
    @GetMapping("/{id}") public RoomResponse get(@PathVariable Long id) {return service.get(id,false);}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RoomResponse create(@Valid @RequestBody RoomInput input) {return service.create(input);}
    @PutMapping("/{id}") public RoomResponse update(@PathVariable Long id,@Valid @RequestBody RoomInput input) {return service.update(id,input);}
    @DeleteMapping("/{id}") public Map<String,Object> delete(@PathVariable Long id) {return service.delete(id);}
}
