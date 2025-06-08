# Stationery-Management (Hệ thống Quản lý Văn phòng phẩm)
Đây là một hệ thống "Quản lý Văn phòng phẩm" toàn diện, được thiết kế theo hướng dịch vụ với kiến trúc **client-server** mạnh mẽ. Hệ thống này cung cấp các giải pháp hiệu quả cho việc đặt hàng sản phẩm và quản lý tồn kho văn phòng phẩm, giúp các tổ chức và cá nhân tối ưu hóa quy trình làm việc.

## Các tính năng chính

* **Đặt hàng dễ dàng**: Người dùng có thể dễ dàng duyệt và đặt mua các sản phẩm văn phòng phẩm thông qua ứng dụng di động hoặc ứng dụng web.
* **Quản lý tồn kho thông minh**: Hệ thống tự động cập nhật và theo dõi số lượng sản phẩm trong kho, đưa ra cảnh báo khi số lượng dưới ngưỡng quy định.
* **Xử lý đơn hàng hiệu quả**: Toàn bộ quá trình xử lý đơn hàng, từ đặt hàng đến thanh toán và giao nhận, đều được quản lý một cách trơn tru.
* **Tích hợp thanh toán MoMo**: Cổng thanh toán MoMo được tích hợp sẵn, mang đến trải nghiệm thanh toán nhanh chóng và an toàn cho người dùng.
* **Quản lý dữ liệu tập trung**: Mọi dữ liệu về sản phẩm, đơn hàng, khách hàng, và tồn kho đều được lưu trữ an toàn và quản lý tập trung trong cơ sở dữ liệu.

## Kiến trúc hệ thống

Hệ thống được xây dựng trên nền tảng **kiến trúc client-server**, đảm bảo tính linh hoạt và khả năng mở rộng.

* **Máy chủ (Server)**:
    * Được phát triển bằng **Spring Boot**, cung cấp các API để xử lý mọi yêu cầu từ các ứng dụng client.
    * Quản lý logic nghiệp vụ, xác thực người dùng, và tương tác với cơ sở dữ liệu.
* **Ứng dụng di động (Mobile App)**:
    * Được phát triển trên nền tảng **Android** sử dụng ngôn ngữ **Java**.
    * Cung cấp giao diện trực quan và thân thiện với người dùng để đặt hàng và theo dõi đơn hàng mọi lúc mọi nơi.
* **Ứng dụng web (Web App)**:
    * Được phát triển bằng **JavaScript**, đảm bảo tính tương thích và khả năng truy cập trên nhiều trình duyệt.
    * Cung cấp dịch vụ giúp dễ dàng đặt hàng và thanh toán.
    * Phục vụ cho việc quản lý tổng thể, bao gồm quản lý sản phẩm, quản lý tồn kho, xem báo cáo, và quản lý người dùng.
* **Cơ sở dữ liệu (Database)**:
    * Sử dụng **MySQL** để lưu trữ tất cả dữ liệu quan trọng của hệ thống một cách có cấu trúc và an toàn.
* **Cổng thanh toán (Payment Gateway)**:
    * Tích hợp **MoMo**, một trong những cổng thanh toán phổ biến nhất tại Việt Nam, để đơn giản hóa quá trình thanh toán cho người dùng.
    * Hỗ trợ thanh toán khi nhận hàng

## Công nghệ sử dụng

* **Backend**: Spring Boot (Java)
* **Mobile App**: Android (Java)
* **Web App**: JavaScript
* **Cơ sở dữ liệu**: MySQL
* **Cổng thanh toán**: MoMo API

## Giao diện ứng dụng

###Giao diện Web App:

* **Trang chủ**:
![Trang chủ 1](https://github.com/user-attachments/assets/0929e62e-7884-4b1d-accf-0fe3884e2864)
![Trang chủ 2](https://github.com/user-attachments/assets/716fa47e-1dfe-4fb0-9d9d-7f48e0746baa)
![Trang chủ 3](https://github.com/user-attachments/assets/8cd3d24d-f4a6-4de6-b64e-093cfa405b0f)
![Trang chủ 4](https://github.com/user-attachments/assets/f5fd2280-f6bc-4ee9-91b4-bfa25dcade52)
![Trang chủ 5](https://github.com/user-attachments/assets/2caa82de-e30e-40b4-8281-4cad7aa15cb8)

* **Sản phẩm**:
![Danh sách sản phẩm](https://github.com/user-attachments/assets/aa0100c4-4c48-4cc8-90a3-1909d9d28a1c)
![Chi tiết sản phẩm](https://github.com/user-attachments/assets/a7520244-bf06-4611-a098-4c9248143f7a)

* **Đặt hàng**:
![Đặt hàng](https://github.com/user-attachments/assets/98a3c614-aed9-40d4-bd39-24698a1635eb)
![Thanh toán MoMo](https://github.com/user-attachments/assets/fd80afae-d0ac-4f95-8cee-404a4fa81f11)

* **Admin**:

![Dashboard](https://github.com/user-attachments/assets/c4605ecf-4767-44cb-ae68-17e6d9c99d23)
![User](https://github.com/user-attachments/assets/58e83a26-d80f-43b1-a8b6-12e26636ee46)
![Sản phẩm](https://github.com/user-attachments/assets/ccf400c9-0e17-4afb-8f64-82b4746cc860)
![Đơn hàng](https://github.com/user-attachments/assets/9af8ed6f-d8f2-4f0e-9e8c-77cde9768ebc)

###Giao diện Android App:

* **Trang chủ**:
![Trang chủ](https://github.com/user-attachments/assets/d1e35886-279a-444e-a043-b70cf81d7019)
![Danh mục](https://github.com/user-attachments/assets/748850c8-8f71-468c-8723-1c733859e23d)
* **Giỏ hàng**:
![Giỏ hàng](https://github.com/user-attachments/assets/0309d7c0-45f7-44d4-962a-0583d57e116a)
* **Sản phẩm**:
![Sản phẩm](https://github.com/user-attachments/assets/c7057a97-e047-4190-a3a7-7d649c80d905)
![Chi tiết sản phẩm](https://github.com/user-attachments/assets/d42d6889-b255-40ab-bd30-8a06c11b6af3)
* **Đặt hàng**:
![Địa chỉ giao hàng](https://github.com/user-attachments/assets/ac169d75-75f3-4d00-a3c3-cf2f44c436ce)
![Phương thức thanh toán](https://github.com/user-attachments/assets/50742835-3a7b-4765-8dd2-2d0bab958ec1)
![Kiểm tra đơn hàng](https://github.com/user-attachments/assets/15580d11-f56f-456f-8bf3-3774801c0c41)
![Thực hiện thanh toán](https://github.com/user-attachments/assets/e8062d4d-aae8-454a-a37e-015bb14dffa7)
