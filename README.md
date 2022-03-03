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
5. flight(flight_id, flight_no, from_airport_id **(FK)**, to_airport_id **(FK)**, airplane_id **(FK)**, arrivaled (tinyint));
6. booking(booking_id, flight_id, seat_number, price, uid);
7. flight_log(flight_log_id, log_date(CURRENT TIMESTAMP), username, flight_id, flight_no_old, flight_no_new);

# NOTE:
1. Xây dựng check chỗ trống để đáp máy bay ở sân bay không?
