# 📄 Software Requirements Specification (SRS) - Real-time Auction System

## 1. User Stories

### 👤 Bidder

- **US.01**: Là một Bidder, tôi muốn đăng ký/đăng nhập để có thể tham gia vào các phiên đấu giá.

- **US.02**: Là một Bidder, tôi muốn xem danh sách sản phẩm đang đấu giá theo thời gian thực (real-time) để biết sản phẩm nào sắp kết thúc.

- **US.03**: Là một Bidder, tôi muốn đặt giá (Place Bid) cho sản phẩm. Giá tôi đặt phải cao hơn giá hiện tại cộng với bước giá tối thiểu.

- **US.04**: Là một Bidder, tôi muốn thấy ngay lập tức khi có người khác trả giá cao hơn tôi mà không cần tải lại trang.

- **US.05**: Là một Bidder, tôi muốn nhận thông báo khi phiên đấu giá kết thúc để biết mình có thắng hay không.

- **US.06**: Là một Bidder, tôi muốn nhận thông báo khi có một sản phẩm được đưa ra đấu giá nằm trong lĩnh vực tôi hứng thú.

- **US.07**: Là một Bidder, tôi muốn hệ thống tự mở một cửa sổ chat với Seller được để thảo luận về thanh toán và nhận hàng.

- **US.08**: Là một Bidder thắng đấu giá, tôi muốn một tin nhắn tự động trong đoạn chat với seller xác nhận món hàng tôi đã thắng. 

---

### 📦 Seller

- **US.09**: Là một Seller, tôi muốn đăng sản phẩm gồm: tên, mô tả, phân loại, giá khởi điểm, bước giá và thời gian kết thúc.

- **US.10**: Là một Seller, tôi muốn quản lý danh sách sản phẩm mình đã đăng để theo dõi tiến độ đấu giá.

- **US.11**: Là một Seller, tôi muốn nhận yêu cầu chat từ người thắng cuộc để hoàn tất giao dịch và vận chuyển nhanh nhất có thể.
---

### ⚙️ System

- **US.12**: Là hệ thống, tôi phải tự động chốt phiên ngay khi hết thời gian đếm ngược và xác định người thắng cuộc duy nhất.

- **US.13**: Là hệ thống, tôi phải đảm bảo tính nhất quán, không cho phép hai người cùng đặt thành công một mức giá ở cùng một thời điểm (Race Condition).

---

### ⚙️ Admin

- **US.14**: Là Admin, tôi có thể quản lý được các user account (Phân quyền, Khóa tài khoản, Gửi cảnh báo, Xóa tài khoản).

- **US.15**: Là Admin, tôi có thể quản lý được các cuộc đấu giá (Xóa đấu giá).

---

## 2. Requirements 

### 2.1. Functional Requirements 

- **F.01 - Auth**: Hệ thống phải hỗ trợ xác thực qua JWT, OAuth2.

- **F.02 - Real-time Engine**: Sử dụng WebSocket (Spring WebSocket) để đẩy dữ liệu giá mới tới toàn bộ người dùng đang xem sản phẩm.

- **F.03 - Bidding Logic**:
  - Kiểm tra phiên đấu giá còn hiệu lực.
  - Kiểm tra số tiền đặt (`New Bid > Current Price + Step Price`).
  - Trừ tiền/đóng băng tiền trong ví người dùng (optional).

- **F.04 - Caching**: Lưu trữ giá cao nhất hiện tại (Current High Bid) vào Redis để truy xuất với tốc độ cực cao.

- **F.05 - Scheduler**: Hệ thống tự động quét các phiên đấu giá đến hạn kết thúc mỗi giây (Sử dụng Spring Scheduler hoặc Redis TTL Event).

- **F.06 - Notification Service**: Hệ thống phải tự động gửi thông báo real-time hoặc push notification khi:
                                    - Người dùng bị outbid.
                                    - Phiên đấu giá kết thúc.
                                    - Có sản phẩm mới theo sở thích của bidder.

- **F.07 - Auction Result**: Hệ thống phải lưu trữ kết quả đấu giá và cho phép người dùng xem lại lịch sử.

- **F.08 - Chat Service**: Hệ thống phải cung cấp chat real-time qua WebSocket giữa Bid Winner và Seller.

- **F.09 - Auto-initiate Conversation**: Khi phiên đấu giá chuyển sang trạng thái CLOSED và có winner_id, hệ thống tự động khởi tạo một bản ghi hội thoại (Conversation) gắn với auction_id.

- **F.10 - System Generated Message**: Hệ thống tự động gửi một tin nhắn định dạng "Announcement" vào hội thoại ngay khi khởi tạo (Ví dụ: "Chúc mừng! Bạn đã thắng đấu giá sản phẩm [Tên SP] với mức giá [Giá]...").

- **F.11 - Admin Management**: Hệ thống phải có một panel riêng cho admin để có thể quản lý các user và các auction.
---

### 2.2. Non-Functional Requirements 

- **NF.01 - Concurrency**: Xử lý ít nhất 500 yêu cầu đặt giá mỗi giây cho cùng một sản phẩm mà không bị lỗi dữ liệu (Sử dụng Redis Distributed Lock).

- **NF.02 - Latency**: Thời gian phản hồi từ khi người dùng nhấn "Bid" đến khi hệ thống xác nhận phải dưới 100ms.

- **NF.03 - Scalability**: Hệ thống phải có khả năng chạy trên nhiều instance server khác nhau (Sử dụng Redis Pub/Sub để đồng bộ socket).

- **NF.04 - Containerization**: Hệ thống phải được đóng gói bằng Docker để đảm bảo môi trường chạy đồng nhất giữa development, testing và production.

- **NF.05 - Deployment**: Hệ thống phải hỗ trợ triển khai bằng Docker Compose hoặc Kubernetes để dễ dàng mở rộng nhiều instance server.

- **NF.06 - Consistency**:
  Hệ thống phải đảm bảo strong consistency cho dữ liệu giá đấu tại một thời điểm.

- **NF.07 - Availability**:
  Hệ thống phải đảm bảo uptime ≥ 99.9% trong quá trình vận hành.


---

## 3. Use Case Specification

---

### UC-01: Register/Login

- **Actor**: User (Guest)  
- **Description**: Người dùng tạo tài khoản hoặc đăng nhập để tham gia hệ thống.  

- **Pre-condition**:
  - Người dùng có kết nối mạng và truy cập vào ứng dụng.

- **Main Flow**:
  1. Người dùng nhập Email/Username và Password.
  2. Hệ thống kiểm tra thông tin trong PostgreSQL.
  3. Hệ thống khởi tạo JWT Token (chứa UserID và Role).
  4. Hệ thống trả về Token cho Client để lưu vào LocalStorage/Cookie.

- **Alternative Flow**:
  - **2a**. Sai thông tin đăng nhập → Hệ thống báo lỗi *"Invalid credentials"*.
  - **2b**. Tài khoản đã tồn tại (khi Register) → Hệ thống báo lỗi *"User already exists"*.

- **Post-condition**:
  - Người dùng nhận được JWT và có quyền truy cập các API yêu cầu xác thực.

---

### UC-02: View Auction List (Real-time)

- **Actor**: Bidder / Seller  
- **Description**: Xem danh sách các phiên đấu giá và nhận cập nhật giá/thời gian tự động.  

- **Pre-condition**:
  - Người dùng đã truy cập vào trang Dashboard/Lobby.

- **Main Flow**:
  1. Client gửi yêu cầu lấy danh sách phiên đấu giá.
  2. Hệ thống lấy dữ liệu từ Redis Cache (hoặc DB nếu cache trống).
  3. Client kết nối (Subscribe) vào WebSocket Room tổng (Lobby).
  4. Hệ thống liên tục đẩy (Broadcast) các thay đổi về giá hoặc thời gian còn lại qua WebSocket.

- **Alternative Flow**:
  - **2a**. Không có phiên đấu giá nào đang diễn ra → Hiển thị *"No active auctions"*.

- **Post-condition**:
  - Người dùng nhìn thấy danh sách sản phẩm với thông tin cập nhật từng giây.

---

### UC-03: Place Bid ⭐ (Critical)

- **Actor**: Bidder  
- **Description**: Người dùng đặt giá cho một sản phẩm đang đấu giá.  

- **Pre-condition**:
  - Người dùng đã đăng nhập (có JWT hợp lệ).
  - Phiên đấu giá đang trạng thái OPEN.
  - Số dư tài khoản đủ để đặt cọc (nếu có).

- **Main Flow**:
  1. Bidder nhập giá và nhấn "Bid".
  2. Hệ thống kiểm tra tính hợp lệ: `New Price > Current Price + Bid Step`.
  3. Hệ thống acquire Redis Distributed Lock theo `auction_id` để tránh Race Condition.
  4. Hệ thống cập nhật giá mới và người dẫn đầu vào Redis.
  5. Hệ thống gửi (Broadcast) giá mới qua WebSocket cho tất cả người đang xem sản phẩm đó.
  6. Hệ thống release lock.
  7. Hệ thống đẩy một Job vào Queue (Async) để ghi lịch sử Bid vào PostgreSQL.

- **Alternative Flow**:
  - **2a**. Giá không hợp lệ → Hệ thống reject và báo lỗi.
  - **3a**. Lock fail → Hệ thống yêu cầu retry hoặc báo *"System busy"*.
  - **5a**. Phiên đấu giá vừa kết thúc → Reject và báo *"Auction closed"*.

- **Post-condition**:
  - Giá mới được cập nhật thành công, người dùng cũ bị *Outbid* nhận thông báo.

---

### UC-04: Create Auction

- **Actor**: Seller  
- **Description**: Người bán đăng sản phẩm mới lên sàn đấu giá.  

- **Pre-condition**:
  - Người dùng có quyền SELLER.

- **Main Flow**:
  1. Seller điền thông tin: Tên, mô tả, ảnh, giá khởi điểm, bước giá, thời gian bắt đầu/kết thúc.
  2. Hệ thống lưu thông tin vào PostgreSQL.
  3. Hệ thống khởi tạo cache trên Redis để chuẩn bị nhận Bid.
  4. Hệ thống thiết lập Scheduler để tự động mở/đóng phiên.

- **Alternative Flow**:
  - **1a**. Thời gian kết thúc < thời gian bắt đầu → Báo lỗi dữ liệu.

- **Post-condition**:
  - Phiên đấu giá mới được tạo và hiển thị trên danh sách chờ.

---

### UC-05: Close Auction (System Auto)

- **Actor**: System (Automated)  
- **Description**: Tự động kết thúc phiên đấu giá khi hết thời gian và xác định người thắng.  

- **Pre-condition**:
  - Thời gian hiện tại ≥ thời gian kết thúc phiên.

- **Main Flow**:
  1. Scheduler kích hoạt (hoặc Redis Key Expired Event).
  2. Hệ thống khóa phiên đấu giá và chuyển trạng thái sang CLOSED.
  3. Hệ thống lấy người trả giá cao nhất từ Redis.
  4. Hệ thống cập nhật kết quả (WinnerID, FinalPrice) vào PostgreSQL.
  5. Hệ thống broadcast thông báo "Auction Ended".
  6. Hệ thống gửi Email/Notification cho Seller và Winner.

- **Alternative Flow**:
  - **3a**. Không có ai đặt giá → trạng thái *"No Winner"*.

- **Post-condition**:
  - Phiên đấu giá kết thúc, sẵn sàng cho xử lý thanh toán.

---

### UC-06: Initiate Post-Auction Chat (System Auto)

- **Actor**: System, Bidder (Winner), Seller
- **Description**: Tự động kết nối người mua và người bán sau khi đấu giá kết thúc.

- **Pre-condition**:
  - Hết thời gian đấu giá và có người thắng đấu giá.

- **Main Flow**:
  1. Khi UC-05 (Close Auction) hoàn tất, hệ thống xác định được winner_id và seller_id.
  2. Hệ thống tạo một mã phòng chat (conversation_id) liên kết với auction_id.
  3. Hệ thống tạo một tin nhắn tự động: "Chúc mừng @Winner đã thắng đấu giá sản phẩm [Product Name]! Vui lòng trao đổi với Seller tại đây."
  4. Winner nhận được Notification. Khi nhấn vào Notification, trình duyệt chuyển hướng (Link) trực tiếp tới UI Chat của phòng vừa tạo.
  5. Cửa sổ chat hiện lên với tin nhắn Popup/Announcement ở đầu danh sách tin nhắn.

- **Post-condition**:
  - Kết nối người thắng đấu giá và người bán, sẵn sàng cho xử lý thanh toán và vận chuyển.

---

## 4. System Architecture

Kiến trúc sử dụng mô hình **Monolithic tinh gọn**, nhưng được thiết kế theo hướng có thể mở rộng sang **Microservices** khi cần.

---

### 🔹 Các thành phần chính

#### 1. Frontend (Client)
- Xây dựng bằng **React / Next.js**.
- Duy trì 2 loại kết nối:
  - **HTTP/REST**: dùng cho các thao tác như login, lấy dữ liệu ban đầu.
  - **WebSocket**: dùng để nhận cập nhật giá real-time.

---

#### 2. API Gateway (Nginx / Spring Cloud Gateway)
- Điều hướng request từ client đến backend.
- Hỗ trợ **Load Balancing** khi chạy nhiều instance backend.
- Có thể xử lý:
  - Routing
  - Authentication (optional)
  - Rate limiting (optional)

---

#### 3. Backend (Spring Boot Application)

- **REST Controllers**:
  - Xử lý các API CRUD cho User, Auction, Auth.

- **WebSocket Handlers**:
  - Quản lý kết nối socket.
  - Phân chia người dùng vào các "Room" theo `auction_id`.

- **Service Layer**:
  - Xử lý logic nghiệp vụ chính:
    - Bidding logic
    - Auction lifecycle
    - Notification

---

#### 4. Redis Layer

- **Pub/Sub**:
  - Đồng bộ message giữa các instance backend.
  - Đảm bảo tất cả user nhận được update real-time.

- **Distributed Lock (Redlock)**:
  - Ngăn race condition khi nhiều người cùng đặt giá.

- **In-memory Cache**:
  - Lưu giá cao nhất hiện tại (`Current High Bid`) của mỗi phiên đấu giá.
  - Giảm tải cho database.

---

#### 5. Database (PostgreSQL)

- Lưu trữ dữ liệu bền vững:
  - User
  - Auction
  - Bid History
  - Transaction (optional)

---

#### 6. Message Queue / Async Worker (Optional)

- Khi có bid thành công:
  - Không ghi trực tiếp vào database (tránh latency cao).
  - Đẩy vào Queue để xử lý bất đồng bộ (Async Write).

- Worker sẽ:
  - Lưu lịch sử bid vào PostgreSQL.
  - Gửi notification (nếu cần).

---

### 🔹 Tổng kết luồng dữ liệu (High-level Flow)

Client → API Gateway → Backend → Redis (Lock + Cache)
                                ↓
                           WebSocket Broadcast
                                ↓
                             Client
                                ↓
                         Async Queue → Database

---

## 6. Database Schema

---

## 6.1. Relational Database (PostgreSQL)

Hệ thống sử dụng PostgreSQL để lưu trữ dữ liệu bền vững, đảm bảo tính toàn vẹn cho các giao dịch và lịch sử.

---

### 6.1.1. Bảng `users`

| Tên cột        | Kiểu dữ liệu     | Ràng buộc                          | Giải thích |
|----------------|------------------|------------------------------------|-----------|
| id             | UUID             | PK, Default: gen_random_uuid()     | Định danh duy nhất |
| username       | VARCHAR(50)      | UNIQUE, NOT NULL                   | Tên đăng nhập |
| email          | VARCHAR(100)     | UNIQUE, NOT NULL                   | Email |
| password_hash  | TEXT             | NOT NULL                           | Mật khẩu mã hóa |
| role           | VARCHAR(20)      | NOT NULL                           | BIDDER, SELLER, ADMIN |
| status         | VARCHAR(20)      | NOT NULL                           | LOCKED, ACTIVE |
| created_at     | TIMESTAMP        | Default: now()                     | Thời gian tạo |
| updated_at     | TIMESTAMP        |

---

### 6.1.2. Bảng `categories`

| Tên cột| Kiểu dữ liệu| Ràng buộc            | Giải thích |
|--------|-------------|----------------------|-----------|
| id     | SERIAL      | PK                   | ID danh mục |
| name   | VARCHAR(100)| UNIQUE, NOT NULL     | Tên danh mục |
| slug   | VARCHAR(100)| UNIQUE               | SEO slug |

---

### 6.1.3. Bảng `user_interests`

| Tên cột   | Kiểu dữ liệu | Ràng buộc              | Giải thích |
|-----------|-------------|------------------------|-----------|
| user_id   | UUID        | FK → users(id)         | Người dùng |
| category_id| INT        | FK → categories(id)    | Danh mục |

**Composite PK**: (user_id, category_id)

---

### 6.1.4. Bảng `auctions`

| Tên cột        | Kiểu dữ liệu     | Ràng buộc                          | Giải thích |
|----------------|------------------|------------------------------------|-----------|
| id             | UUID             | PK                                 | ID phiên |
| seller_id      | UUID             | FK → users(id)                     | Người bán |
| category_id    | INT              | FK → categories(id)                | Phân loại |
| title          | VARCHAR(255)     | NOT NULL                           | Tiêu đề |
| description    | TEXT             |                                    | Mô tả |
| ImageURL       | TEXT             |                                    | Link ảnh |
| start_price    | DECIMAL(19,4)    | NOT NULL, CHECK > 0                | Giá khởi điểm |
| bid_step       | DECIMAL(19,4)    | NOT NULL, CHECK > 0                | Bước giá |
| current_price  | DECIMAL(19,4)    | CHECK > start_price                | Sync từ Redis (eventual consistency) |
| winner_id      | UUID             | FK → users(id), NULLABLE           | Người thắng |
| status         | VARCHAR(20)      | NOT NULL                           | PENDING, OPEN, CLOSED |
| start_at       | TIMESTAMP        | NOT NULL                           | Bắt đầu |
| end_at         | TIMESTAMP        | NOT NULL, CHECK end_at > start_at  | Kết thúc |
| updated_at     | TIMESTAMP        |

---

### 6.1.5. Bảng `bid_history`

| Tên cột   | Kiểu dữ liệu   | Ràng buộc                 | Giải thích |
|-----------|----------------|---------------------------|-----------|
| id        | BIGSERIAL      | PK                        | ID bid |
| auction_id| UUID           | FK, INDEXED               | Phiên |
| user_id   | UUID           | FK → users(id)            | Người bid |
| amount    | DECIMAL(19,4)  | NOT NULL                  | Giá |
| created_at| TIMESTAMP      | Default: now()            | Thời gian |

---

### 6.1.6. Bảng `notifications`

| Tên cột   | Kiểu dữ liệu   | Ràng buộc                 | Giải thích |
|-----------|----------------|---------------------------|-----------|
| id        | BIGSERIAL      | PK                        | ID |
| user_id   | UUID           | FK, INDEXED               | Người nhận |
| type      | VARCHAR(50)    | NOT NULL                  | OUTBID, WIN, NEW_ITEM, ALERT |
| message   | TEXT           | NOT NULL                  | Nội dung |
| is_read   | BOOLEAN        | Default: false            | Trạng thái |
| created_at| TIMESTAMP      | Default: now()            | Thời gian |

---


### 6.1.7. Bảng `conversations`

| Tên cột          | Kiểu dữ liệu   | Ràng buộc                 | Giải thích |
|------------------|----------------|---------------------------|-----------|
| id               | BIGSERIAL      | PK                        | ID |
| auction_id       | UUID           | FK                        | Liên kết với phiên đấu giá |
| participant_one  | UUID           | FK                        | Thường là Seller |
| participant_two  | UUID           | FK                        | Thường là Winner |
| created_at       | TIMESTAMP      | Default: now()            |

---

### 6.1.8. Bảng `messages`

| Tên cột          | Kiểu dữ liệu   | Ràng buộc                 | Giải thích |
|------------------|----------------|---------------------------|-----------|
| id               | BIGSERIAL      | PK                        | ID |
| conversation_id  | UUID           | FK                        | Thuộc phòng chat nào |
| sender_id        | UUID           | FK                        | NULL nếu là tin nhắn hệ thống |
| message_type     | VARCHAR(20)    |                           | TEXT, SYSTEM_NOTIFICATION |
| content          | TEXT           | NOT NULL                  | Nội dung tin nhắn |
| is_read          | BOOLEAN        | Default: false            |
| created_at       | TIMESTAMP      | Default: now()            |

---

### Suggested Indexes

- INDEX (auction_id, amount DESC) ON bid_history
- INDEX (user_id, is_read) ON notifications
- INDEX (status, end_at) ON auctions

---

## 6.2. In-Memory Database (Redis)

Dùng để xử lý các tác vụ yêu cầu tốc độ cao và concurrency.

| Đối tượng        | Key Pattern              | Kiểu dữ liệu | Mục đích |
|------------------|--------------------------|--------------|---------|
| Giá hiện tại     | auction:{id}:price       | String       | Giá cao nhất |
| Winner hiện tại  | auction:{id}:winner      | String       | User dẫn đầu |
| Thông tin nhanh  | auction:{id}:info        | Hash         | end_at, bid_step |
| Lịch sử Bid      | auction:{id}:bids        | ZSet         | Score = amount, Member = userId:timestamp |
| Khóa phân tán    | lock:auction:{id}        | String (TTL) | Redlock chống race condition |   

