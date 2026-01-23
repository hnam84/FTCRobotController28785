package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@TeleOp(name = "Mecanum TeleOp", group = "TeleOp")
public class MecanumTeleOp extends LinearOpMode {

    HardwareRobot robot = new HardwareRobot();

    @Override
    public void runOpMode() {
        robot.init(hardwareMap);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Joystick trái: y cho tiến/lùi, x cho sang ngang
            double y = -gamepad1.left_stick_y; // Tiến (lên) / lùi (xuống)
            double x = gamepad1.left_stick_x;   // Sang trái / phải
            double rx = 0; // Xoay, mặc định 0

            // Thêm deadzone cho joystick (để tránh drift nếu không chạm)
            if (Math.abs(y) < 0.1) y = 0;
            if (Math.abs(x) < 0.1) x = 0;

            // Nếu joystick không dùng (y = 0), dùng dpad up/down cho tiến/lùi
            if (y == 0) {
                if (gamepad1.dpad_up) y = 1;   // Tiến
                if (gamepad1.dpad_down) y = -1; // Lùi
            }

            // Nút dpad left/right cho xoay
            if (gamepad1.dpad_left) rx = -1; // Xoay trái
            if (gamepad1.dpad_right) rx = 1;  // Xoay phải

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

            // Gọi phương thức từ hardware để đặt tốc độ
            robot.setMecanumPower(frontLeftPower, frontRightPower, backLeftPower, backRightPower);

            // Telemetry để debug
            telemetry.addData("Y (Tiến/Lùi)", y);
            telemetry.addData("X (Sang ngang)", x);
            telemetry.addData("RX (Xoay)", rx);
            telemetry.addData("Front Left Power", frontLeftPower);
            telemetry.update();
        }
    }
}