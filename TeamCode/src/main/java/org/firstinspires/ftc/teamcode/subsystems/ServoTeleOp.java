package org.firstinspires.ftc.teamcode.subsystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "ServoTeleOp", group = "Linear OpMode")
public class ServoTeleOp extends BaseOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Chờ start
        waitForStartWithTelemetry();

        // Vòng lặp chính
        while (opModeIsActive()) {
            // Điều khiển servo gạt lên xuống
            if (gamepad1.x) {
                // Nút X: Gạt xuống (close position)
                robot.servo1.setPosition(Constants.SERVO1_CLOSE);
                robot.servo1.setPosition(Constants.SERVO1_OPEN);
// Thay servo1 bằng servo2 hoặc servo3 nếu cần
            } else if (gamepad1.y) {

            }

            // Telemetry để debug
            telemetry.addData("Servo Position", robot.servo1.getPosition());
            telemetry.addData("X Button Pressed", gamepad1.x);
            telemetry.addData("Y Button Pressed", gamepad1.y);
            telemetry.update();

            sleep(20);  // Kiểm soát vòng lặp
        }

        // stopRobot() tự động gọi
    }

}

