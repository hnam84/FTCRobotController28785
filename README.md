# FTCRobotController28785
mã code của đội src 28785 nha
                                                                                   FTC TEAM PROGRAMMING GUIDE (2025-2026)
Chào mừng các thành viên đội lập trình! Đây là tài liệu hướng dẫn quy trình làm việc để đảm bảo code của robot luôn sạch sẽ, ổn định và không bị xung đột.

📁 1. Cấu trúc Source Code (Project Structure)
Để tránh làm rối code, mọi người bắt buộc phải tạo file đúng vị trí:

teamcode/RobotHardware.java: File duy nhất chứa khai báo phần cứng (Motor, Servo, Sensor).

teamcode/Constants.java: Chứa các hằng số (Tốc độ, vị trí Servo, thông số PID).

teamcode/subsystems/: Chứa logic của từng bộ phận (Arm, Intake, Drivetrain).

teamcode/opmodes/teleop/: File điều khiển bằng tay cho Driver.

teamcode/opmodes/auto/: File chạy tự động (RoadRunner/Autonomous).

teamcode/utils/: Các công cụ hỗ trợ (Vision/Math).

teamcode/mấy file khác/ : là mấy file anh tiếc chưa định xóa

🛠 2. Quy tắc Lập trình (Coding Rules)
Tuyệt đối không sử dụng hardwareMap.get() trong file OpMode cá nhân. Tất cả phải gọi thông qua đối tượng robot từ file RobotHardware.

Naming Convention (Quy tắc đặt tên):

Biến & Hàm: camelCase (ví dụ: armMotor, liftUp()).

Hằng số: UPPER_SNAKE_CASE (ví dụ: MAX_POWER, SERVO_OPEN).

File OpMode: [Tên_Thành_Viên]_[Chức_Năng] (ví dụ: Nam_TeleOp_V1.java).

Sử dụng Constants.java: Không được ghi trực tiếp các con số "lạ" vào code (Hardcode). Ví dụ: Thay vì servo.setPosition(0.45), hãy dùng servo.setPosition(Constants.CLAW_OPEN).

🔄 3. Quy trình làm việc với Git (Git Workflow)
Chúng ta sử dụng quy trình Branching để không ai đè code lên ai:

Nhánh main: Nhánh cực kỳ quan trọng, chỉ chứa code đã test chạy tốt trên robot thật. Cấm commit trực tiếp vào đây.

Nhánh cá nhân: Mỗi thành viên tạo một nhánh riêng từ main để làm việc (Ví dụ: dev-an, dev-binh).

Quy trình gộp code (Merge):

Bước 1: Hoàn thành code trên nhánh cá nhân.

Bước 2: Test trên robot hoặc mô phỏng.

Bước 3: Tạo một Pull Request (PR) trên GitHub.

Bước 4: Leader (là Nam đzai) sẽ review code. Nếu ổn, tớ sẽ gộp vào main.

🚀 4. Các bước khi bắt đầu một buổi Test
Sync Code: Luôn Pull code mới nhất từ nhánh main về máy mình trước khi bắt đầu viết bất cứ thứ gì.

Log Data: Sử dụng telemetry.addData() để theo dõi các thông số quan trọng (Encoder, Sensor) nhằm mục đích debug.

An toàn là trên hết: Luôn có một người cầm sẵn nút Stop trên Driver Station hoặc tắt công tắc robot khi chạy thử nghiệm code mới.
                                 Lưu ý từ Leader: "Code chạy được là tốt, nhưng code sạch và người khác đọc hiểu được thì còn tốt hơn. Chúc team 28785 chúng ta có một mùa giải thành công!"
