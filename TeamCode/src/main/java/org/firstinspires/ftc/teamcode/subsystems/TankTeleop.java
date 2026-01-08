package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "TankTeleOp", group = "Linear OpMode")git
public class TankTeleop extends BaseOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot bằng hàm sẵn có từ BaseOpMode
        initRobot();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính cho tank drive
        while (opModeIsActive()) {
            // Đọc giá trị trục Y của 2 joystick (tank drive: left joystick cho bánh trái, right joystick cho bánh phải)
            double leftStickY = gamepad1.left_stick_y;   // Trục Y joystick trái cho bánh trái
            double rightStickY = gamepad1.right_stick_y; // Trục Y joystick phải cho bánh phải

            // Áp dụng deadzone từ Constants để tránh rung
            if (Math.abs(leftStickY) < Constants.JOYSTICK_DEADZONE) leftStickY = 0;
            if (Math.abs(rightStickY) < Constants.JOYSTICK_DEADZONE) rightStickY = 0;

            // Tính toán power cho 2 bánh (nhân với MAX_DRIVE_SPEED từ Constants, và dấu trừ để khớp chuẩn FTC: đẩy lên = tiến)
            double leftPower = -leftStickY * Constants.MAX_DRIVE_SPEED;
            double rightPower = -rightStickY * Constants.MAX_DRIVE_SPEED;

            // Đặt power cho 2 drive motors
            robot.leftDriveMotor.setPower(leftPower);
            robot.rightDriveMotor.setPower(rightPower);

            // Hiển thị telemetry cho debug
            telemetry.addData("Left Stick Y", leftStickY);
            telemetry.addData("Right Stick Y", rightStickY);
            telemetry.addData("Left Motor Power", leftPower);
            telemetry.addData("Right Motor Power", rightPower);
            telemetry.update();

            // Thêm sleep để kiểm soát tốc độ vòng lặp (20ms)
            sleep(20);
        }

        // stopRobot() sẽ được gọi tự động từ BaseOpMode khi kết thúc
    }

    private boolean opModeIsActive() {
        return false;
    }
}