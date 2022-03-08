# POSTMAN TEAM ACC  
> User/Pass: teamanxin
# Actor  
1. Người dùng
2. Máy bay

# Đặc tả  
1. Một email ứng với một tài khoản, xác thực tài khoản qua email  
2. Một chuyến bay chỉ ứng với một máy bay
3. Một chuyến bay sẽ bay đến nhiều sân bay
4. Một sân bay sẽ có nhiều máy bay
5. Một máy bay ứng với nhiều chuyến bay
6. Một hành khách ứng với nhiều chuyến bay
7. Một chuyến bay ứng với nhiều hành khách

# Tables:  
1. users (uid **(AI-PK)**, email, username, password, email_verified, dob, gender, phone, role) 0: passenger | 1:admin
2. airplane (airplane_id **(AI-PK)**, airplane_name, capacity)
3. airport_geo(airport_id **(AI-PK)**, location_id **(FK)**, airport_name, description)
4. location(location_id **(AI-PK)**, country_code, city_id, city_name, country_name, long, lat);
5. flight(flight_id, flight_no, from_airport_id **(FK)**, to_airport_id **(FK)**, airplane_id **(FK)**);
6. flight_schedule(flight_schedule_id **(AI-PK)**, start_time, end_time, flight_no **(FK)**, avaialble_seat);
7. ticket(ticket_id, flight_schedule_id, seat_number, price, uid);
8. flight_log(flight_log_id, log_date(CURRENT TIMESTAMP), username, flight_id, flight_no_old, flight_no_new);

## Note 1:
> **LOCATION**: vị trí của các sân bay trên toàn thế giới
> 
> **FLIGHT**: Chuyến bay được cấu hình sẵn (VD: máy bay Boeing 747 (airplane_name) mang số hiệu (airplane_id) bay từ sân bay Cam Ranh (from_airport_id) đến sân bay TSN (to_airport_id). Với mỗi chủng loại máy bay, một lịch trình sẽ là một chuyến bay riêng (cặp khoá *airplane_id, from_airport_id, to_airport_id*)
> 
> **FLIGHT_SCHEDULE**: Lịch trình bay ứng với chuyến bay mang số hiệu VN123(flight_no) với **timestamp** (start_time, end_time) và còn trống **avaialble_seat**
> 
> **TICKET**: các lượt vé bán ra ứng với flight_schedule_id => INSERT hoặc UDPATE đều phải UPDATE lại trường **avaialble_seat** của bảng **flight_schedule**
> 
> **Các sự kiện đều phải ghi vào bảng FLIGHT_LOG**

## Note 2:
> 1. Lấy dữ liệu tổng quan về chuyến bay => Click vào sẽ gọi API lấy dữ liệu chi tiết chuyến bay (số chỗ ngồi còn trống, lịch trình bay, loại máy bay, thời gian, ...) => Giảm tải nhẹ query

## Note 3:
1. Xây dựng check chỗ trống để đáp máy bay ở sân bay không?

# List API

### User

| Completed | Method | Function         | URL                  | Request data                      | Response data                                                      |
|-----------|--------|------------------|----------------------|-----------------------------------|--------------------------------------------------------------------|
|           | POST   | Login            | /user/login          | {email: string, password: string} | {code: 200, result: {token: <token here>}}                         |
|           | POST   | Register         | /user/signup         | {ssid: "", ...}                   | {code: 200, result: {msg: "ok"}} {code: 400, result: {msg: <err>}} |
|           | POST   | Update user info | /user/update         | {ssid: "", ...}                   | {code: 200, result: {msg: "ok"}} {code: 400, result: {msg: <err>}} |
|           | GET    | Verify email     | /verify/....         |                                   | {code: 200, result: {msg: "ok"}} {code: 400, result: {msg: <err>}} |
|           | POST   | Forget password  | /user/reset_password | {ssid: "", ...}                   | {code: 200, result: {msg: "ok"}} {code: 400, result: {msg: <err>}} |
|           | POST   | Logout           | /user/logout         | {ssid: ""}                        | {code: 200, result: {msg: "ok"}} {code: 400, result: {msg: <err>}} |
|           |        |                  |                      |                                   | {code: 200, result: {msg: "ok"}} {code: 400, result: {msg: <err>}} |

### Booking
  
| Completed | Method | Function                | URL            | Request data                                                     | Response data                   | Note                                                                                                            |
|-----------|--------|-------------------------|----------------|------------------------------------------------------------------|---------------------------------|-----------------------------------------------------------------------------------------------------------------|
|           | POST   | Booking                 | /book/create   |                                                                  |                                 |                                                                                                                 |
|           | POST   | Change flight           | /flight/update |                                                                  |                                 | Thêm sự kiện trong bảng **flight log**                                                                          |
|           | POST   | Cancel booking          | /book/cancel   |                                                                  |                                 |                                                                                                                 |
|           | POST   | Find flight             | /flight/search |                                                                  |                                 | Tìm kiếm dựa trên theo tiêu chí: thời gian khởi hành, kết thúc, địa điểm xuất phát, địa điểm đến, mã chuyến bay |
|           | POST   | Get list flight by time | /flight/list   | {ssid: string, start_time: <DD/MM/YYYY>, end_time: <DD/MM/YYYY>} | {code: 200, result: {list: []}} |                                                                                                                 |
