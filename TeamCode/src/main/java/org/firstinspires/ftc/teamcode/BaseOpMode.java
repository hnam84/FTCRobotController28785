package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

// 1. Phải extends LinearOpMode và nên để là abstract
public abstract class BaseOpMode extends LinearOpMode {

    // Instance của HardwareRobot
    protected HardwareRobot robot = new HardwareRobot();

    // Hàm khởi tạo robot
    protected void initRobot() {
        // Sử dụng hardwareMap có sẵn từ LinearOpMode
        robot.init(hardwareMap);
        telemetry.addData("Status", "Robot Initialized");
        telemetry.update();
    }

    // Hàm tiện ích: Chờ start
    protected void waitForStartWithTelemetry() {
        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();

        // Gọi hàm waitForStart() của lớp cha (LinearOpMode)
        waitForStart();

        telemetry.addData("Status", "Started!");
        telemetry.update();
    }

    // Hàm dừng robot
    protected void stopRobot() {
        robot.stop();
        telemetry.addData("Status", "Robot Stopped");
        telemetry.update();
    }

    // 2. Bây giờ @Override sẽ hết đỏ vì LinearOpMode có hàm này
    @Override
    public void runOpMode() throws InterruptedException {
        // Hàm này sẽ được các lớp con (như Autonomous hoặc TeleOp) viết chi tiết
    }
}