package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

public class BaseOpMode {

    // Instance của HardwareRobot để subclass sử dụng
    protected HardwareRobot robot = new HardwareRobot();

    // Hàm khởi tạo robot (gọi trong subclass để setup hardware)
    protected void initRobot() {
        robot.init(hardwareMap);
        telemetry.addData("Status", "Robot Initialized");
        telemetry.update();
    }

    // Hàm tiện ích: Chờ start với telemetry (hiển thị "Waiting for start...")
    protected void waitForStartWithTelemetry() {
        telemetry.addData("Status", "Waiting for start...");
        telemetry.update();
        waitForStart();
        telemetry.addData("Status", "Started!");
        telemetry.update();
    }

    public void waitForStart() {
    }

    // Hàm dừng robot (gọi trong subclass nếu cần, hoặc tự động trong stop)
    protected void stopRobot() {
        robot.stop();
        telemetry.addData("Status", "Robot Stopped");
        telemetry.update();
    }

    // Override runOpMode để đảm bảo stopRobot được gọi khi kết thúc
    @Override
    public void runOpMode() throws InterruptedException {
        // Subclass sẽ override và gọi initRobot() ở đây
        // Logic chính của subclass
        // sau tự fill

        // Đảm bảo dừng robot khi OpMode kết thúc
        stopRobot();
    }
}
