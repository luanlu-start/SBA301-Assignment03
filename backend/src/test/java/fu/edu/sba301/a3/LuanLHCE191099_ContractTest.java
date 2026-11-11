package fu.edu.sba301.a3;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class LuanLHCE191099_ContractTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    JsonNode call(String method, String path, Object body, String token, int expected)
        throws Exception {
        var builder = request(HttpMethod.valueOf(method), path).contentType(
            MediaType.APPLICATION_JSON
        );
        if (body != null) builder.content(mapper.writeValueAsBytes(body));
        if (token != null) builder.header("Authorization", "Bearer " + token);
        MvcResult result = mvc.perform(builder).andReturn();
        assertEquals(
            expected,
            result.getResponse().getStatus(),
            path + ": " + result.getResponse().getContentAsString()
        );
        String text = result.getResponse().getContentAsString();
        if (expected == 204) {
            assertTrue(text.isBlank());
            return mapper.nullNode();
        }
        JsonNode payload = mapper.readTree(text);
        assertTrue(payload.path("success").isBoolean());
        assertEquals(expected < 400, payload.path("success").asBoolean());
        assertFalse(payload.path("message").asText().isBlank());
        assertTrue(payload.has("data"));
        return payload.get("data");
    }

    String login(String email, String password) throws Exception {
        return call(
            "POST",
            "/api/auth/login",
            Map.of("email", email, "password", password),
            null,
            200
        )
            .get("token")
            .asText();
    }

    String unique() {
        return "test" + System.nanoTime();
    }

    String staff() throws Exception {
        return login("staff@hotel.local", "Staff@123");
    }

    String customer() throws Exception {
        return login("customer@hotel.local", "Customer@123");
    }

    String day(int n) {
        return LocalDate.now().plusDays(n).toString();
    }

    Map<String, Object> booking(List<Long> rooms, int start, int end) {
        return Map.of("roomIds", rooms, "startDate", day(start), "endDate", day(end), "status", 1);
    }

    Map<String, Object> staffBooking(List<Long> rooms, int start, int end, int status) {
        return Map.of(
            "customerId",
            1,
            "roomIds",
            rooms,
            "startDate",
            day(start),
            "endDate",
            day(end),
            "status",
            status
        );
    }

    Map<String, Object> registration(String email) {
        return Map.of(
            "fullName",
            "Test Customer",
            "telephone",
            "0901234567",
            "email",
            email,
            "birthday",
            "2000-01-01",
            "password",
            "Test@123"
        );
    }

    @Test
    @Transactional(
        propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED
    )
    void roomsArePublicButManagementIsProtected() throws Exception {
        JsonNode roomRows = call("GET", "/api/public/rooms", null, null, 200);
        assertFalse(roomRows.isEmpty());
        for (JsonNode row : roomRows) {
            assertFalse(row.get("roomTypeId").isNull());
            assertFalse(row.get("roomTypeName").isNull());
        }
        call("GET", "/api/customers", null, null, 401);
        call("GET", "/api/customers", null, customer(), 403);
        call("GET", "/api/my/profile", null, staff(), 403);
        call("GET", "/api/my/profile", null, "bad.jwt", 401);
    }

    @Test
    void customerRegistrationLoginAndDuplicateEmail() throws Exception {
        String email = unique() + "@example.com";
        JsonNode account = call("POST", "/api/auth/register", registration(email), null, 201);
        assertFalse(account.has("password"));
        assertFalse(login(email, "Test@123").isBlank());
        call("POST", "/api/auth/register", registration(email), null, 409);
        call("POST", "/api/auth/login", Map.of("email", email, "password", "wrong"), null, 401);
    }

    @Test
    @Transactional(
        propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED
    )
    void multiRoomBookingCalculatesPriceOnServerAndCanBeEdited() throws Exception {
        String c = customer(),
            s = staff();
        JsonNode created = call("POST", "/api/bookings", booking(List.of(1L, 2L), 10, 12), c, 201);
        assertEquals(2, created.get("details").size());
        assertEquals(2200000, created.get("totalPrice").asDouble());
        long id = created.get("id").asLong();
        try {
            JsonNode updated = call(
                "PUT",
                "/api/bookings/" + id,
                staffBooking(List.of(2L, 3L), 10, 13, 1),
                s,
                200
            );
            assertEquals(4500000, updated.get("totalPrice").asDouble());
            assertEquals(2, updated.get("details").size());
            JsonNode saved = call("GET", "/api/bookings/" + id, null, c, 200);
            assertEquals("customer@hotel.local", saved.path("customerEmail").asText());
            assertFalse(saved.has("customer"));
            for (JsonNode detail : saved.path("details")) {
                assertFalse(detail.path("roomNumber").asText().isBlank());
                assertTrue(detail.path("subtotal").isNumber());
                assertFalse(detail.has("reservation"));
            }
            assertFalse(call("GET", "/api/bookings", null, s, 200).isEmpty());
            assertFalse(call("GET", "/api/my/bookings", null, c, 200).isEmpty());
        } finally {
            call("DELETE", "/api/bookings/" + id, null, s, 204);
        }
        call("GET", "/api/bookings/" + id, null, c, 404);
    }

    @Test
    void overlapsAreRejectedButCheckoutBoundaryIsAllowed() throws Exception {
        String c = customer(),
            s = staff();
        long id = call("POST", "/api/bookings", booking(List.of(1L), 20, 22), c, 201)
            .get("id")
            .asLong();
        call("POST", "/api/bookings", booking(List.of(1L), 21, 23), c, 409);
        JsonNode available = call(
            "GET",
            "/api/public/rooms?startDate=" + day(21) + "&endDate=" + day(22),
            null,
            null,
            200
        );
        assertFalse(available.findValuesAsText("id").contains("1"));
        call("POST", "/api/bookings", booking(List.of(1L), 22, 24), c, 201);
        call("PUT", "/api/bookings/" + id, staffBooking(List.of(1L), 21, 23, 1), s, 409);
        call("PUT", "/api/bookings/" + id, staffBooking(List.of(1L), 20, 22, 0), s, 200);
        call("POST", "/api/bookings", booking(List.of(1L), 20, 22), c, 201);
    }

    @Test
    void failedMultiRoomBookingDoesNotReserveTheOtherRoom() throws Exception {
        String c = customer();
        call("POST", "/api/bookings", booking(List.of(1L), 30, 32), c, 201);
        call("POST", "/api/bookings", booking(List.of(1L, 2L), 30, 32), c, 409);
        call("POST", "/api/bookings", booking(List.of(2L), 30, 32), c, 201);
    }

    @Test
    void deletingUsedRoomDeactivatesItAndKeepsHistory() throws Exception {
        String c = customer(),
            s = staff();
        call("POST", "/api/bookings", booking(List.of(1L), 40, 42), c, 201);
        JsonNode result = call("DELETE", "/api/rooms/1", null, s, 200);
        assertTrue(result.get("deactivated").asBoolean());
        call("GET", "/api/public/rooms/1", null, null, 404);
        assertEquals(0, call("GET", "/api/rooms/1", null, s, 200).get("status").asInt());
        assertEquals(1, call("GET", "/api/my/bookings", null, c, 200).size());
        call("POST", "/api/bookings", booking(List.of(1L), 43, 44), c, 409);
    }

    @Test
    @Transactional(
        propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED
    )
    void unusedRoomsCanBeDeletedAndRoomValidationIsEnforced() throws Exception {
        String s = staff();
        Map<String, Object> room = new LinkedHashMap<>(
            Map.of(
                "number",
                unique(),
                "description",
                "Test room",
                "capacity",
                2,
                "roomTypeId",
                1,
                "status",
                1,
                "price",
                123456
            )
        );
        JsonNode created = call("POST", "/api/rooms", room, s, 201);
        long id = created.get("id").asLong();
        try {
            assertEquals("Standard", created.get("roomTypeName").asText());
            room.put("roomTypeId", 2);
            JsonNode updated = call("PUT", "/api/rooms/" + id, room, s, 200);
            assertEquals(2L, updated.get("roomTypeId").asLong());
            assertEquals("Deluxe", updated.get("roomTypeName").asText());
        } finally {
            JsonNode deleted = call("DELETE", "/api/rooms/" + id, null, s, 200);
            assertFalse(deleted.get("deactivated").asBoolean());
        }
        call("GET", "/api/rooms/" + id, null, s, 404);
        room.put("price", -1);
        JsonNode errors = call("POST", "/api/rooms", room, s, 400);
        assertTrue(errors.path("price").isTextual());
    }

    @Test
    @Transactional(
        propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED
    )
    void customerCannotReadOrCreateAnotherCustomersBooking() throws Exception {
        String c = customer();
        long id = call("POST", "/api/bookings", booking(List.of(1L), 50, 52), c, 201)
            .get("id")
            .asLong();
        String email = unique() + "@example.com";
        long otherId = call("POST", "/api/auth/register", registration(email), null, 201)
            .get("id")
            .asLong();
        String other = login(email, "Test@123");
        call("GET", "/api/bookings/" + id, null, other, 403);
        assertTrue(call("GET", "/api/my/bookings", null, other, 200).isEmpty());
        call("POST", "/api/bookings", staffBooking(List.of(2L), 50, 52, 1), other, 403);
        call("DELETE", "/api/bookings/" + id, null, staff(), 204);
        call("DELETE", "/api/customers/" + otherId, null, staff(), 204);
    }

    @Test
    void profileAndBookingRejectSpoofedFieldsAndInvalidDates() throws Exception {
        String c = customer();
        call(
            "PUT",
            "/api/my/profile",
            Map.of(
                "fullName",
                "Test",
                "telephone",
                "0901234567",
                "email",
                "customer@hotel.local",
                "birthday",
                "2000-01-01",
                "role",
                "STAFF"
            ),
            c,
            400
        );
        call("POST", "/api/bookings", booking(List.of(1L), 60, 59), c, 400);
        Map<String, Object> body = new LinkedHashMap<>(booking(List.of(1L), 60, 62));
        body.put("totalPrice", 1);
        call("POST", "/api/bookings", body, c, 400);
    }

    @Test
    void staffCanManageCustomersButBookedCustomersCannotBeDeleted() throws Exception {
        String s = staff(),
            email = unique() + "@example.com";
        Map<String, Object> input = new LinkedHashMap<>(registration(email));
        input.put("status", 1);
        long id = call("POST", "/api/customers", input, s, 201).get("id").asLong();
        input.put("fullName", "Changed name");
        input.put("password", "");
        call("PUT", "/api/customers/" + id, input, s, 200);
        call("DELETE", "/api/customers/" + id, null, s, 204);
        call("POST", "/api/bookings", booking(List.of(1L), 70, 72), customer(), 201);
        call("DELETE", "/api/customers/1", null, s, 409);
    }
}
