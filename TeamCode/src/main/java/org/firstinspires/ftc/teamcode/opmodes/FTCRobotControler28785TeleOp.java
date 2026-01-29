package org.firstinspires.ftc.teamcode.opmodes;



import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "FTCRobotControler28785TeleOp", group = "TeleOp")
public class FTCRobotControler28785TeleOp extends BaseOpMode {

    // Biến cho PID sort (từ SortV2TeleOp)
    static final double TICKS_PER_REV = 288.0;
    static final double TICKS_PER_DEGREE = TICKS_PER_REV / 360.0;

    double kP = 0.02;
    double kI = 0.0;
    double kD = 0.0008;
    double kS = 0.08;   // power tối thiểu thắng ma sát

    double integral = 0;
    double lastError = 0;
    long lastTime = 0;

    boolean rotated = false;

    // Biến để track trạng thái servo1 (toggle)
    boolean servo1Open = false; // Ban đầu đóng (SERVO1_CLOSE)

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Khởi tạo biến thời gian cho PID
        lastTime = System.nanoTime();

        telemetry.addLine("Ready - Gamepad1: Mecanum & Intake, Gamepad2: Sort, Shooter & Servo");
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

            // ==================== PHẦN SORT (Gamepad2) ====================
            // Bấm B quay 120 độ (chỉ 1 lần)
            if (gamepad2.b && !rotated) {
                rotateToAnglePID(120);
                rotated = true;
            }

            // Reset bằng nút A
            if (gamepad2.a) {
                robot.sortMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                robot.sortMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                rotated = false;
            }

            // ==================== PHẦN SHOOTER (Gamepad2) ====================
            double shooterPower = 0.0;

            // Nếu nhấn bất kỳ nút dpad nào (up, down, left, right), cả hai shooter quay cùng chiều với SHOOTER_SPEED
            if (gamepad2.dpad_up || gamepad2.dpad_down || gamepad2.dpad_left || gamepad2.dpad_right) {
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
            // Bấm R2 để toggle servo1 giữa đóng và mở (0.0 và 0.5, giả sử 90 độ)
            if (gamepad2.right_trigger > 0.1) { // R2 pressed
                servo1Open = !servo1Open; // Toggle trạng thái
                if (servo1Open) {
                    robot.servo1.setPosition(Constants.SERVO1_OPEN); // Mở (0.5)
                } else {
                    robot.servo1.setPosition(Constants.SERVO1_CLOSE); // Đóng (0.0)
                }
                // Nghỉ một chút để tránh toggle liên tục nếu giữ nút
                sleep(200);
            }

            // ==================== TELEMETRY ====================
            telemetry.addData("Y (Tiến/Lùi)", y);
            telemetry.addData("X (Sang ngang)", x);
            telemetry.addData("RX (Xoay)", rx);
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Intake Power", intakePower);
            telemetry.addData("Sort Encoder", robot.sortMotor.getCurrentPosition());
            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Servo1 Position", servo1Open ? Constants.SERVO1_OPEN : Constants.SERVO1_CLOSE);
            telemetry.update();

            // Nghỉ 20ms để tránh quá tải CPU
            idle();
        }

        // Dừng robot khi kết thúc
        stopRobot();
    }

    // Phương thức quay sort bằng PID (từ SortV2TeleOp, điều chỉnh để dùng robot.sortMotor)
    void rotateToAnglePID(double angle) {
        double targetTicks = angle * TICKS_PER_DEGREE;

        integral = 0;
        lastError = 0;
        lastTime = System.nanoTime();

        while (opModeIsActive()) {
            double current = robot.sortMotor.getCurrentPosition();
            double error = targetTicks - current;

            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1e9;
            lastTime = now;

            integral += error * deltaTime;
            double derivative = (error - lastError) / deltaTime;
            lastError = error;

            double power = (kP * error) + (kI * integral) + (kD * derivative);

            // Minimum power thắng ma sát
            if (Math.abs(power) < kS) {
                power = Math.signum(power) * kS;
            }

            // Giới hạn công suất
            power = Math.max(-0.4, Math.min(0.4, power));
            robot.sortMotor.setPower(power);

            telemetry.addData("Sort Target (ticks)", targetTicks);
            telemetry.addData("Sort Current (ticks)", current);
            telemetry.addData("Sort Error", error);
            telemetry.addData("Sort Power", power);
            telemetry.update();

            // Sai số nhỏ thì dừng
            if (Math.abs(error) < 3) break;

            idle();
        }

        robot.sortMotor.setPower(0);
    }
}