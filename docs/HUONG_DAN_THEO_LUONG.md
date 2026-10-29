# Assignment 03 - Đọc theo luồng và từng file

## 1. Mở app

1. Backend: `LuanLHCE191099_Application.java` gọi SpringApplication.run.
2. Spring đọc application.properties và file local, mở DataSource, Hibernate tạo/cập nhật bảng trong database riêng.
3. `config/LuanLHCE191099_SeedData.java` tạo dữ liệu mẫu nếu chưa có dữ liệu khởi tạo.
4. Frontend: index.html nạp `src/LuanLHCE191099_main.jsx`, main render App.
5. `src/LuanLHCE191099_App.jsx` ghép Router, AuthProvider và menu. Trang đầu hiển thị dữ liệu công khai.

## 2. Đăng nhập

```text
LoginPage: Form.onSubmit
-> useAuth().login(email, password)
-> api.post('/auth/login')
-> AuthController
-> AuthService kiểm tra tài khoản/password
-> JwtService ký token
-> trả token + UserPrincipal không có password
-> AuthProvider lưu token vào sessionStorage và đổi state user
-> Router chuyển tới màn hình phù hợp role
```

File cần mở theo thứ tự:

| Bước | File source |
|---|---|
| Gõ và Submit | frontend/src/LuanLHCE191099_LoginPage.jsx |
| Lưu phiên và cập nhật React | frontend/src/LuanLHCE191099_Auth.jsx |
| Gửi request, gắn Authorization | frontend/src/LuanLHCE191099_api.js |
| Nhận Email/Password | backend controller/LuanLHCE191099_AuthController.java |
| Xác minh tài khoản | backend service/LuanLHCE191099_AuthService.java |
| Tạo token | backend security/LuanLHCE191099_JwtService.java |

Khi request sau gửi lên, JwtFilter kiểm tra chữ ký/hạn dùng/issuer, nạp lại người dùng rồi đặt Authentication vào SecurityContext. SecurityConfig và @PreAuthorize quyết định endpoint nào được gọi.

## 3. Đọc danh sách và tìm kiếm

```text
Page được Router chọn
-> useApiList(endpoint + '?q=...')
-> Axios GET kèm token nếu có
-> Controller -> Service -> Repository -> SQL Server
-> Service chuyển Entity thành Response DTO trong transaction
-> JSON response -> state của hook -> JSX map thành bảng/card
```

`LuanLHCE191099_useApiList.js` hủy request cũ khi đổi từ khóa/page để tránh response cũ ghi đè kết quả mới. Entity dùng getter để đọc quan hệ Hibernate; không trả Entity trực tiếp ra JSON.

## 4. CRUD cơ bản

Mở `frontend/src/LuanLHCE191099_CrudPage.jsx`:

1. Nút Add gọi open() để tạo bản nháp từ các field.
2. FormFields nối value/onChange với state form.
3. Submit kiểm tra HTML input rồi POST/PUT JSON tới API.
4. @Valid kiểm tra request DTO ở backend. Service kiểm tra quy tắc nghiệp vụ trong transaction.
5. Repository lưu dữ liệu, Service trả DTO; frontend đóng popup và reload danh sách.
6. Edit lấy id dòng làm editingId; payload được chọn theo các field của form, không gửi nguyên response object.
7. Delete mở xác nhận trước. API thành công thì refresh; lỗi ràng buộc hiện trong hộp xác nhận để người dùng hiểu vì sao chưa xóa.

API mới là nơi lưu dữ liệu chính. State React giữ dữ liệu để hiển thị; sessionStorage chỉ giữ token đăng nhập.

## 5. Theo dõi một booking nhiều phòng

Mở PublicRooms.jsx và BookingService.java:

```text
Customer chọn ngày + nhiều roomIds -> Confirm booking
-> POST /api/bookings
-> BookingController lấy UserPrincipal từ token
-> BookingService xác định Customer của chính người gọi
-> kiểm tra ngày, Customer và danh sách phòng
-> khóa từng Room theo ID tăng dần
-> kiểm tra BookingDetail trùng khoảng ngày của booking Confirmed
-> tính ActualPrice và TotalPrice từ RoomPricePerDay ở server
-> lưu BookingReservation + BookingDetail trong một transaction
-> response -> UI hiện mã booking và tổng tiền
```

Điều kiện trùng lịch: `existing.startDate < requested.endDate && existing.endDate > requested.startDate`. Checkout không chiếm đêm tiếp theo. Nếu một phòng lỗi, toàn bộ booking không được lưu một phần.

BookingDetailId chứa reservationId + roomId. @EmbeddedId và @MapsId ánh xạ khóa ghép với hai quan hệ. Khi sửa, service giữ lại detail cho phòng vẫn chọn, bỏ detail bị gỡ và thêm detail cho phòng mới.

Staff dùng BookingPage.jsx để tạo offline: chọn thêm Customer. Customer không được gửi customerId của người khác. TotalPrice không nằm trong request DTO.

## 6. Xóa phòng và lịch sử

```text
RoomPage xác nhận Delete -> DELETE /api/rooms/{id}
-> RoomService khóa Room
-> BookingDetailRepository kiểm tra đã từng có detail chưa
-> chưa có: xóa Room
-> đã có: đổi RoomStatus=0, giữ detail cũ
-> UI hiện thông báo phù hợp và reload
```

MyController lấy CustomerId từ token để đọc /api/my/bookings và profile. GET /api/bookings/{id} cũng kiểm tra chủ sở hữu khi người gọi là Customer. Staff quản lý được toàn bộ booking; Customer không được gọi các endpoint quản lý Staff.

## 7. Cách tự lần theo code

Chọn một thao tác rồi trả lời lần lượt: sự kiện bắt đầu ở đâu -> handler nào nhận -> request gì được gửi -> Controller nào nhận -> Service kiểm tra gì -> Repository đọc/ghi bảng nào -> response gì về -> state/UI đổi ra sao.

Backend có 3 tầng chính Controller -> Service -> Repository. DTO, Entity và Security hỗ trợ các tầng đó; chúng không phải các màn hình frontend.

Khi gặp lỗi, xem request trong DevTools Network trước: URL, method, payload, status và message. Sau đó mở đúng service liên quan. Trong SQL Server, chạy SELECT ở database/LuanLHCE191099_verification.sql để xác nhận dữ liệu đã lưu.
