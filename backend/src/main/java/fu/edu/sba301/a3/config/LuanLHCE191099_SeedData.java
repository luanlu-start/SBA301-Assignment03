package fu.edu.sba301.a3.config;
import fu.edu.sba301.a3.entity.*;
import fu.edu.sba301.a3.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
@Component @ConditionalOnProperty(name="app.seed.enabled",havingValue="true",matchIfMissing=true)
public class LuanLHCE191099_SeedData implements ApplicationRunner {
    private final LuanLHCE191099_CustomerRepository customers;private final LuanLHCE191099_RoomRepository rooms;private final LuanLHCE191099_RoomTypeRepository types;private final PasswordEncoder encoder;
    public LuanLHCE191099_SeedData(LuanLHCE191099_CustomerRepository customers,LuanLHCE191099_RoomRepository rooms,LuanLHCE191099_RoomTypeRepository types,PasswordEncoder encoder) {this.customers=customers;this.rooms=rooms;this.types=types;this.encoder=encoder;}
    @Override @Transactional public void run(ApplicationArguments args) {
        if(types.count()==0) {
            LuanLHCE191099_RoomType standard=new LuanLHCE191099_RoomType();standard.name="Standard";standard.description="A comfortable room for a short stay";standard.note="Wi-Fi included";types.save(standard);
            LuanLHCE191099_RoomType deluxe=new LuanLHCE191099_RoomType();deluxe.name="Deluxe";deluxe.description="More space for a relaxed stay";deluxe.note="City view";types.save(deluxe);
            addRoom("101",standard,2,"550000.00","A quiet standard room with a double bed.");
            addRoom("102",standard,2,"550000.00","Twin beds, desk and private bathroom.");
            addRoom("201",deluxe,4,"950000.00","A spacious deluxe room for families.");
            addRoom("202",deluxe,3,"850000.00","Deluxe room with natural light and a seating area.");
        }
        if(customers.count()==0) {
            LuanLHCE191099_Customer c=new LuanLHCE191099_Customer();c.fullName="Le Hoang Luan";c.email="customer@hotel.local";c.telephone="0901234567";c.birthday=LocalDate.of(2004,1,1);c.status=1;c.password=encoder.encode("Customer@123");customers.save(c);
        }
    }
    private void addRoom(String number,LuanLHCE191099_RoomType type,int capacity,String price,String description) {LuanLHCE191099_Room r=new LuanLHCE191099_Room();r.number=number;r.type=type;r.capacity=capacity;r.price=new BigDecimal(price);r.description=description;r.status=1;rooms.save(r);}
}
