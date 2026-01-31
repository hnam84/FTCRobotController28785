package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "FTCRobotControler28785TeleOpVer2", group = "TeleOp")
public class FTCRobotControler28785TeleOpVer2 extends BaseOpMode {

    boolean modeSort = true;
    boolean isServoActionActive = false;
    long servoActionStartTime = 0;
    final long SERVO_ACTION_DURATION = 500000000; // 500ms in nanoseconds

    // Thêm biến cho debounce toggle modeSort
    boolean lastTogglePress = false;
    long lastToggleTime = 0;
    final long TOGGLE_DEBOUNCE_TIME = 200000000; // 200ms debounce

    // Thêm biến cho field-centric driving
    private IMU imu;
    private double botHeading = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Khởi tạo IMU cho field-centric driving
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientation = new RevHubOrientationOnRobot(
                Constants.IMU_LOGO_DIRECTION, Constants.IMU_USB_DIRECTION);
        imu.initialize(new IMU.Parameters(orientation));
        imu.resetYaw(); // Reset yaw để bắt đầu từ 0

        // Khởi tạo servo2 ở góc 1
        robot.servo2.setPosition(Constants.SERVO2_POSITION1_INTAKE);

        telemetry.addLine("Ready - Gamepad1: Mecanum & Intake, Gamepad2: Sort (Servo2), Shooter & Servo1");
        telemetry.update();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính
        while (opModeIsActive()) {
            // ==================== PHẦN DI CHUYỂN MECANUM (Gamepad1) ====================
            // Cập nhật heading từ IMU cho field-centric
            YawPitchRollAngles angles = imu.getRobotYawPitchRollAngles();
            botHeading = angles.getYaw(AngleUnit.RADIANS);

            // Joystick trái: y cho tiến/lùi, x cho sang ngang
            double y = -gamepad1.left_stick_y; // Tiến (lên) / lùi (xuống)
            double x = gamepad1.left_stick_x;   // Sang trái / phải
            double rx = gamepad1.right_stick_x; // Xoay với joystick phải

            // Áp dụng deadzone từ Constants
            if (Math.abs(y) < Constants.JOYSTICK_DEADZONE) y = 0;
            if (Math.abs(x) < Constants.JOYSTICK_DEADZONE) x = 0;
            if (Math.abs(rx) < Constants.JOYSTICK_DEADZONE) rx = 0;

            // Field-centric: Chuyển đổi từ driver perspective sang robot perspective
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            // Tính tốc độ cho 4 bánh mecanum (field-centric)
            double frontLeftPower = rotY + rotX + rx;
            double frontRightPower = rotY - rotX - rx;
            double backLeftPower = rotY - rotX + rx;
            double backRightPower = rotY + rotX - rx;

            // Scale để không vượt quá 1.0
            double max = Math.max(Math.abs(frontLeftPower), Math.max(Math.abs(frontRightPower),
                    Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));
            if (max > 1.0) {
                frontLeftPower  /= max;
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
            // Toggle chế độ với debounce để nhanh nhạy hơn
            boolean currentToggle = gamepad2.a;
            long currentTime = System.nanoTime();
            if (currentToggle && !lastTogglePress && (currentTime - lastToggleTime) > TOGGLE_DEBOUNCE_TIME) {
                modeSort = !modeSort;
                lastToggleTime = currentTime;
            }
            lastTogglePress = currentToggle;

            // Điều khiển servo2 dựa trên chế độ (nhanh nhạy hơn, không sleep)
            if (modeSort) {
                // MODE 1: Chỉnh các phần intake
                if (gamepad2.x) {
                    robot.servo2.setPosition(Constants.SERVO2_POSITION1_INTAKE);
                } else if (gamepad2.y) {
                    robot.servo2.setPosition(Constants.SERVO2_POSITION2_INTAKE);
                } else if (gamepad2.b) {
                    robot.servo2.setPosition(Constants.SERVO2_POSITION3_INTAKE);
                }
            } else {
                // Chế độ 2: X (góc 4), Y (góc 5), B (góc 6)
                if (gamepad2.x) {
                    robot.servo2.setPosition(Constants.SERVO2_POSITION1_SHOOTER);
                } else if (gamepad2.y) {
                    robot.servo2.setPosition(Constants.SERVO2_POSITION2_SHOOTER);
                } else if (gamepad2.b) {
                    robot.servo2.setPosition(Constants.SERVO2_POSITION3_SHOOTER);
                }
            }

            // ==================== PHẦN SHOOTER (Gamepad2) ====================
            double shooterPower = 0.0;

            // Đơn giản hóa: Sử dụng bumper cho tốc độ thấp, trigger cho tốc độ cao
            if (gamepad2.left_bumper) {
                shooterPower = Constants.NEAR_SHOOTER_SPEED; // Tốc độ thấp
            } else if (gamepad2.left_trigger > 0.1) {
                shooterPower = Constants.FAR_SHOOTER_SPEED; // Tốc độ cao
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
            telemetry.addData("Bot Heading (deg)", Math.toDegrees(botHeading));
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.addData("Intake Power", intakePower);
            telemetry.addData("Shooter Power", shooterPower);
            telemetry.addData("Mode Sort (Intake/Shooter)", modeSort ? "Intake" : "Shooter");
            telemetry.addData("Servo1 Action Active", isServoActionActive);
            telemetry.update();

            // Nghỉ 10ms để tránh quá tải CPU (nhanh nhạy hơn)
            idle();
        }

        // Dừng robot khi kết thúc
        stopRobot();
    }
}
