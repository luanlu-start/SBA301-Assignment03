USE [A3LuanLH_SE1910];
GO
SELECT CustomerID, CustomerFullName, EmailAddress, CustomerStatus FROM Customer;
SELECT r.RoomID, r.RoomNumber, t.RoomTypeName, r.RoomStatus, r.RoomPricePerDay FROM RoomInformation r JOIN RoomType t ON r.RoomTypeID=t.RoomTypeID;
SELECT b.BookingReservationID, c.CustomerFullName, b.BookingStatus, b.TotalPrice FROM BookingReservation b JOIN Customer c ON b.CustomerID=c.CustomerID;
SELECT d.BookingReservationID, r.RoomNumber, d.StartDate, d.EndDate, d.ActualPrice FROM BookingDetail d JOIN RoomInformation r ON d.RoomID=r.RoomID;
