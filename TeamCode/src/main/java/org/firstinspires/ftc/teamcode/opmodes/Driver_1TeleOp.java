package org.firstinspires.ftc.teamcode.opmodes;



import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "Driver_1TeleOp", group = "TeleOp")
public class Driver_1TeleOp  extends BaseOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính
        while (opModeIsActive()) {
            // ==================== PHẦN DI CHUYỂN MECANUM ====================
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

            // ==================== PHẦN INTAKE ====================
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

            // ==================== TELEMETRY ====================
            telemetry.addData("Y (Tiến/Lùi)", y);
            telemetry.addData("X (Sang ngang)", x);
            telemetry.addData("RX (Xoay)", rx);
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Intake Power", intakePower);
            telemetry.update();

            // Nghỉ 20ms để tránh quá tải CPU
            idle();
        }

        // Dừng robot khi kết thúc
        stopRobot();
    }
}