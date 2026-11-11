package fu.edu.sba301.a3.config;

import fu.edu.sba301.a3.entity.*;
import fu.edu.sba301.a3.repository.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class LuanLHCE191099_SeedData implements ApplicationRunner {

    private final LuanLHCE191099_CustomerRepository customers;
    private final LuanLHCE191099_RoomRepository rooms;
    private final LuanLHCE191099_RoomTypeRepository types;
    private final PasswordEncoder encoder;

    public LuanLHCE191099_SeedData(
        LuanLHCE191099_CustomerRepository customers,
        LuanLHCE191099_RoomRepository rooms,
        LuanLHCE191099_RoomTypeRepository types,
        PasswordEncoder encoder
    ) {
        this.customers = customers;
        this.rooms = rooms;
        this.types = types;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (types.count() == 0) {
            LuanLHCE191099_RoomType standard = LuanLHCE191099_RoomType.builder()
                .name("Standard")
                .description("A comfortable room for a short stay")
                .note("Wi-Fi included")
                .build();
            types.save(standard);
            LuanLHCE191099_RoomType deluxe = LuanLHCE191099_RoomType.builder()
                .name("Deluxe")
                .description("More space for a relaxed stay")
                .note("City view")
                .build();
            types.save(deluxe);
            addRoom("101", standard, 2, "550000.00", "A quiet standard room with a double bed.");
            addRoom("102", standard, 2, "550000.00", "Twin beds, desk and private bathroom.");
            addRoom("201", deluxe, 4, "950000.00", "A spacious deluxe room for families.");
            addRoom(
                "202",
                deluxe,
                3,
                "850000.00",
                "Deluxe room with natural light and a seating area."
            );
        }
        if (customers.count() == 0) {
            LuanLHCE191099_Customer customer = LuanLHCE191099_Customer.builder()
                .fullName("Le Hoang Luan")
                .email("customer@hotel.local")
                .telephone("0901234567")
                .birthday(LocalDate.of(2004, 1, 1))
                .status(1)
                .password(encoder.encode("Customer@123"))
                .build();
            customers.save(customer);
        }
    }

    private void addRoom(
        String number,
        LuanLHCE191099_RoomType type,
        int capacity,
        String price,
        String description
    ) {
        LuanLHCE191099_Room room = LuanLHCE191099_Room.builder()
            .number(number)
            .type(type)
            .capacity(capacity)
            .price(new BigDecimal(price))
            .description(description)
            .status(1)
            .build();
        rooms.save(room);
    }
}
