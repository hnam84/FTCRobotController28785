package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "FTCRobotControler28785TeleOp", group = "TeleOp")
public class FTCRobotControler28785TeleOp extends BaseOpMode {

    // Biến cho PID sort (loại bỏ vì không dùng motor nữa)

    // Biến để track trạng thái servo1 (thay đổi từ toggle sang hành động tạm thời)
    boolean isServoActionActive = false;
    long servoActionStartTime = 0;
    final long SERVO_ACTION_DURATION = 500000000; // 500ms in nanoseconds

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Khởi tạo servo2 ở góc 1
        robot.servo2.setPosition(Constants.SERVO2_POSITION1);

        telemetry.addLine("Ready - Gamepad1: Mecanum & Intake, Gamepad2: Sort (Servo2), Shooter & Servo1");
        telemetry.update();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính
        while (opModeIsActive()) {
            // ==================== PHẦN DI CHUYỂN MECANUM (Gamepad1) ====================
            // Joystick trái: y cho tiến/lùi, x cho sang ngang
            double y = -gamepad1.left_stick_y; // Tiến (lên) / lùi (xuống)
            double x = gamepad1.left_stick_x;   // Sang trái / phải
            double rx = 0; // Xoay, mặc định 0

            // Áp dụng deadzone từ Constants
            if (Math.abs(y) < Constants.JOYSTICK_DEADZONE) y = 0;
            if (Math.abs(x) < Constants.JOYSTICK_DEADZONE) x = 0;

            // Nếu joystick không dùng (y = 0), dùng dpad up/down HOẶC y/a cho tiến/lùi
            if (y == 0) {
                if (gamepad1.dpad_up || gamepad1.y) y = 1;   // Tiến
                if (gamepad1.dpad_down || gamepad1.a) y = -1; // Lùi
            }

            // Nút dpad left/right HOẶC x/b cho xoay
            if (gamepad1.dpad_left || gamepad1.x) rx = -1; // Xoay trái
            if (gamepad1.dpad_right || gamepad1.b) rx = 1;  // Xoay phải

            // Tính tốc độ cho 4 bánh mecanum
            double frontLeftPower = y + x + rx;
            double frontRightPower = y - x - rx;
            double backLeftPower = y - x + rx;
            double backRightPower = y + x - rx;

            // Scale để không vượt quá 1.0
            double max = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(frontRightPower),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));
            if (max > 1.0) {
                frontLeftPower /= max;
                frontRightPower /= max;
                backLeftPower /= max;
                backRightPower /= max;
            }

            // Nhân với MAX_DRIVE_SPEED từ Constants để giới hạn tốc độ tối đa
            frontLeftPower *= Constants.MAX_DRIVE_SPEED;
            frontRightPower *= Constants.MAX_DRIVE_SPEED;
            backLeftPower *= Constants.MAX_DRIVE_SPEED;
            backRightPower *= Constants.MAX_DRIVE_SPEED;

            // Gọi phương thức từ hardware để đặt tốc độ
            robot.setMecanumPower(frontLeftPower, frontRightPower, backLeftPower, backRightPower);

            // ==================== PHẦN INTAKE (Gamepad1) ====================
            double intakePower = 0.0;

            // Sử dụng R2 cho quay cùng chiều (như A cũ), L2 cho quay ngược chiều (như B cũ)
            if (gamepad1.right_trigger > 0.1) { // R2 (right_trigger)
                intakePower = Constants.INTAKE_SPEED; // Quay cùng chiều
            } else if (gamepad1.left_trigger > 0.1) { // L2 (left_trigger)
                intakePower = -Constants.INTAKE_SPEED; // Quay ngược chiều
            }

            // Đặt power cho intake motor
            if (robot != null && robot.intakeMotor != null) {
                robot.intakeMotor.setPower(intakePower);
            }

            // ==================== PHẦN SORT (Gamepad2, dùng servo2) ====================
            // Điều khiển servo2 bằng X (góc 1), Y (góc 2), B (góc 3)
            if (gamepad2.x) {
                robot.servo2.setPosition(Constants.SERVO2_POSITION1);
            } else if (gamepad2.y) {
                robot.servo2.setPosition(Constants.SERVO2_POSITION2);
            } else if (gamepad2.b) {
                robot.servo2.setPosition(Constants.SERVO2_POSITION3);
            }

            // ==================== PHẦN SHOOTER (Gamepad2) ====================
            double shooterPower = 0.0;

            // Nếu nhấn bất kỳ nút dpad nào (up, down, left, right) HOẶC L2, cả hai shooter quay cùng chiều với SHOOTER_SPEED
            if (gamepad2.dpad_up || gamepad2.dpad_down || gamepad2.dpad_left || gamepad2.dpad_right || gamepad2.left_trigger > 0.1) {
                shooterPower = Constants.SHOOTER_SPEED; // Quay cùng chiều (forward)
            }

            // Đặt power cho cả hai shooter motors
            if (robot.shooterMotor1 != null) {
                robot.shooterMotor1.setPower(shooterPower);
            }
            if (robot.shooterMotor2 != null) {
                robot.shooterMotor2.setPower(shooterPower);
            }

            // ==================== PHẦN SERVO1 (Gamepad2) ====================
            // Khi nhấn R2, servo1 quay 90 độ (từ close sang open), chờ 500ms, rồi quay lại close
            if (gamepad2.right_trigger > 0.1 && !isServoActionActive) {
                isServoActionActive = true;
                servoActionStartTime = System.nanoTime();
                robot.servo1.setPosition(Constants.SERVO1_OPEN); // Quay 90 độ (mở)
            }

            // Kiểm tra nếu hành động servo đang active và thời gian đã đủ 500ms, quay lại vị trí cũ
            if (isServoActionActive && (System.nanoTime() - servoActionStartTime) > SERVO_ACTION_DURATION) {
                robot.servo1.setPosition(Constants.SERVO1_CLOSE); // Quay lại vị trí cũ (đóng)
                isServoActionActive = false;
            }

            // ==================== TELEMETRY ====================
            telemetry.addData("Y (Tiến/Lùi)", y);
            telemetry.addData("X (Sang ngang)", x);
            telemetry.addData("RX (Xoay)", rx);
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Intake Power", intakePower);
            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Servo1 Action Active", isServoActionActive);
            telemetry.update();

            // Nghỉ 20ms để tránh quá tải CPU
            idle();
        }

        // Dừng robot khi kết thúc
        stopRobot();
    }
}