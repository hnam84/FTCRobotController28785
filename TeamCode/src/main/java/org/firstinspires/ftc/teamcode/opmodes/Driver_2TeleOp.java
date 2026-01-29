package org.firstinspires.ftc.teamcode.opmodes;



import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "Driver_2TeleOp", group = "TeleOp")
public class Driver_2TeleOp extends BaseOpMode {

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

        telemetry.addLine("Ready - Press B to rotate sort 120 degrees, use D-pad for shooters, R2 for servo1");
        telemetry.update();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính
        while (opModeIsActive()) {
            // ==================== PHẦN SORT ====================
            // Bấm B quay 120 độ (chỉ 1 lần)
            if (gamepad1.b && !rotated) {
                rotateToAnglePID(120);
                rotated = true;
            }

            // Reset bằng nút A
            if (gamepad1.a) {
                robot.sortMotor.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                robot.sortMotor.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                rotated = false;
            }

            // ==================== PHẦN SHOOTER ====================
            double shooterPower = 0.0;

            // Nếu nhấn bất kỳ nút dpad nào (up, down, left, right), cả hai shooter quay cùng chiều với SHOOTER_SPEED
            if (gamepad1.dpad_up || gamepad1.dpad_down || gamepad1.dpad_left || gamepad1.dpad_right) {
                shooterPower = Constants.SHOOTER_SPEED; // Quay cùng chiều (forward)
            }

            // Đặt power cho cả hai shooter motors
            if (robot.shooterMotor1 != null) {
                robot.shooterMotor1.setPower(shooterPower);
            }
            if (robot.shooterMotor2 != null) {
                robot.shooterMotor2.setPower(shooterPower);
            }

            // ==================== PHẦN SERVO1 ====================
            // Bấm R2 để toggle servo1 giữa đóng và mở (0.0 và 0.5, giả sử 90 độ)
            if (gamepad1.right_trigger > 0.1) { // R2 pressed
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
