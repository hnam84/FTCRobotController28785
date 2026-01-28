package org.firstinspires.ftc.teamcode.subsystems;


import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "SortTeleOp", group = "TeleOp")
public class SortTeleOp extends BaseOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Chờ start
        waitForStartWithTelemetry();

        // Loop chính
        while (opModeIsActive()) {
            // Điều khiển servo1 với nút X (lên/open) và Y (xuống/close)
            if (gamepad1.x) {
                robot.servo1.setPosition(Constants.SERVO1_OPEN);  // Lên
            } else if (gamepad1.y) {
                robot.servo1.setPosition(Constants.SERVO1_CLOSE);  // Xuống
            }

            // Điều khiển motor sort với nút B (quay 120 độ)
            if (gamepad1.b) {
                // Tính toán target position: 120 độ = (120/360) * MOTOR_CPR ≈ 186.67 ticks, làm tròn thành 187
                int currentPosition = robot.sortMotor.getCurrentPosition();
                int targetTicks = (int) ((120.0 / 360.0) * Constants.MOTOR_CPR);  // ≈ 187 ticks
                int targetPosition = currentPosition + targetTicks;

                // Set target và chạy
                robot.sortMotor.setTargetPosition(targetPosition);
                robot.sortMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.sortMotor.setPower(0.5);  // Tốc độ quay (có thể thêm constant nếu cần)
            }

            // Telemetry để debug
            telemetry.addData("Servo1 Position", robot.servo1.getPosition());
            telemetry.addData("Sort Motor Position", robot.sortMotor.getCurrentPosition());
            telemetry.addData("Sort Motor Target", robot.sortMotor.getTargetPosition());
            telemetry.addData("Sort Motor Busy", robot.sortMotor.isBusy());
            telemetry.update();
        }

        // Dừng robot
        stopRobot();
    }
}