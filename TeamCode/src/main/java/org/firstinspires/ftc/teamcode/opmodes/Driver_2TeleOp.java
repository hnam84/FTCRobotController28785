package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "Driver_2TeleOp", group = "TeleOp")
public class Driver_2TeleOp extends BaseOpMode {

    // Biến để track trạng thái servo1 (hành động tạm thời)
    boolean isServoActionActive = false;
    boolean modeSort = true;
    long servoActionStartTime = 0;
    final long SERVO_ACTION_DURATION = 500000000; // 500ms in nanoseconds

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Khởi tạo servo2 ở góc 1
        robot.servo2.setPosition(Constants.SERVO2_POSITION1_INTAKE);

        telemetry.addLine("Ready - Gamepad2: Sort (Servo2), Shooter & Servo1");
        telemetry.update();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính
        while (opModeIsActive()) {
            // ==================== PHẦN SORT (Gamepad2, dùng servo2) ====================
            if (gamepad2.a) {
                modeSort = !modeSort; // Toggle chế độ
                sleep(200);
            }
            // Điều khiển servo2 dựa trên chế độ
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

            // Nếu nhấn bất kỳ nút dpad nào (up, down, left, right) HOẶC L2, cả hai shooter quay cùng chiều với SHOOTER_SPEED
            if (gamepad2.left_bumper) {
                if (gamepad2.dpad_up || gamepad2.dpad_down || gamepad2.dpad_left || gamepad2.dpad_right) {
                    shooterPower = Constants.NEAR_SHOOTER_SPEED; // Quay cùng chiều (forward) với tốc độ thấp
                }
            }
            if (gamepad2.left_trigger > 0.1) {
                if (gamepad2.dpad_up || gamepad2.dpad_down || gamepad2.dpad_left || gamepad2.dpad_right) {
                    shooterPower = Constants.FAR_SHOOTER_SPEED; // Quay cùng chiều (forward) với tốc độ cao
                }
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