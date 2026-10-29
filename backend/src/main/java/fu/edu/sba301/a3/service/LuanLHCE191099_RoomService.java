package fu.edu.sba301.a3.service;
import static fu.edu.sba301.a3.dto.LuanLHCE191099_Dtos.*;
import fu.edu.sba301.a3.entity.LuanLHCE191099_Room;
import fu.edu.sba301.a3.exception.LuanLHCE191099_ApiException;
import fu.edu.sba301.a3.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service @Transactional(readOnly=true)
public class LuanLHCE191099_RoomService {
    private final LuanLHCE191099_RoomRepository rooms;private final LuanLHCE191099_RoomTypeRepository types;private final LuanLHCE191099_BookingDetailRepository details;
    public LuanLHCE191099_RoomService(LuanLHCE191099_RoomRepository rooms,LuanLHCE191099_RoomTypeRepository types,LuanLHCE191099_BookingDetailRepository details) {this.rooms=rooms;this.types=types;this.details=details;}
    public List<RoomResponse> list(String q,boolean activeOnly,LocalDate startDate,LocalDate endDate) {
        if((startDate==null)!=(endDate==null) || (startDate!=null && !endDate.isAfter(startDate))) throw new LuanLHCE191099_ApiException(400,"Provide a valid start and end date together.");
        String k=q.trim().toLowerCase(Locale.ROOT);
        return rooms.findAll(Sort.by("number")).stream().filter(r -> !activeOnly || r.status==1)
            .filter(r -> (r.number+" "+r.type.name+" "+r.description).toLowerCase(Locale.ROOT).contains(k))
            .filter(r -> startDate==null || details.countOverlap(r.id,startDate,endDate,null)==0).map(LuanLHCE191099_RoomService::view).toList();
    }
    public RoomResponse get(Long id,boolean publicOnly) {LuanLHCE191099_Room r=required(id);if(publicOnly && r.status!=1) throw new LuanLHCE191099_ApiException(404,"Room not found.");return view(r);}
    public List<RoomTypeResponse> types() {return types.findAll(Sort.by("id")).stream().map(t -> new RoomTypeResponse(t.id,t.name,t.description,t.note)).toList();}
    @Transactional public RoomResponse create(RoomInput input) {LuanLHCE191099_Room r=new LuanLHCE191099_Room();apply(r,input);return view(rooms.saveAndFlush(r));}
    @Transactional public RoomResponse update(Long id,RoomInput input) {LuanLHCE191099_Room r=rooms.findLockedById(id).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"Room not found."));apply(r,input);return view(rooms.saveAndFlush(r));}
    @Transactional public Map<String,Object> delete(Long id) {
        LuanLHCE191099_Room r=rooms.findLockedById(id).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"Room not found."));
        if(details.existsByRoom_Id(id)) {r.status=0;rooms.saveAndFlush(r);return Map.of("message","Room has booking history and was set inactive.","deactivated",true);}
        rooms.delete(r);rooms.flush();return Map.of("message","Room deleted.","deactivated",false);
    }
    private void apply(LuanLHCE191099_Room r,RoomInput input) {
        String number=input.number().trim();rooms.findByNumber(number).filter(other -> !other.id.equals(r.id)).ifPresent(other -> {throw new LuanLHCE191099_ApiException(409,"Room number already exists.");});
        r.number=number;r.description=input.description();r.capacity=input.capacity();r.status=input.status();r.price=input.price();r.type=types.findById(input.roomTypeId()).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"Room type not found."));
    }
    private LuanLHCE191099_Room required(Long id) {return rooms.findById(id).orElseThrow(() -> new LuanLHCE191099_ApiException(404,"Room not found."));}
    public static RoomResponse view(LuanLHCE191099_Room r) {return new RoomResponse(r.id,r.number,r.description,r.capacity,r.type.id,r.type.name,r.status,r.price);}
}
