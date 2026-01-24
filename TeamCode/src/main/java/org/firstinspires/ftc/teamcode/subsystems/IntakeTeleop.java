package org.firstinspires.ftc.teamcode.subsystems;


import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.gamepad1;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants; // Quan trọng: Phải import Constants để dùng được INTAKE_SPEED

@TeleOp(name = "IntakeTeleOp", group = "Linear OpMode")

public class IntakeTeleop extends BaseOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot (hàm này phải được định nghĩa trong BaseOpMode)
        initRobot();

        // Chờ start - Đảm bảo BaseOpMode có hàm này, nếu không hãy dùng waitForStart();
        waitForStart();

        // Vòng lặp chính
        while (opModeIsActive()) { // Bây giờ hàm này sẽ hết báo đỏ vì đã extends BaseOpMode
            double intakePower = 0.0;

            // Kiểm tra gamepad1 (BaseOpMode đã có sẵn gamepad1 nên không cần import static phức tạp)
            if (gamepad1.a) {
                intakePower = Constants.INTAKE_SPEED;
            } else if (gamepad1.b) {
                intakePower = -Constants.INTAKE_SPEED;
            }

            // Đặt power cho motor (Đảm bảo biến 'robot' đã được khai báo public/protected trong BaseOpMode)
            //if (robot != null && robot.intakeMotor != null) {
                //robot.intakeMotor.setPower(intakePower);
            }

            // Hiển thị telemetry
            //telemetry.addData("Intake Power", intakePower);
            telemetry.update();

            // Nghỉ 20ms để tránh quá tải CPU
            idle(); // Trong FTC nên dùng idle() hoặc sleep(20)
        }
    }

