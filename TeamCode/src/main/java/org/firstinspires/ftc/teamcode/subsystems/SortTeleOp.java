package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;

@TeleOp(name = "SortTeleOp", group = "TeleOp")
public class SortTeleOp extends BaseOpMode {

    // Biến để track góc hiện tại (0: 0°, 1: 120°, 2: 240°)
    private int currentAngleIndex = 0;
    private final int[] targetTicks = {0, 187, 374};  // Ticks tương ứng: 0°, 120°, 240° (187 ticks ≈ 120°)
    private boolean bPressedLast = false;  // Để tránh nhấn liên tục

    @Override
    public void runOpMode() throws InterruptedException {
        // Khởi tạo robot
        initRobot();

        // Chờ start
        waitForStartWithTelemetry();

        // Loop chính
        while (opModeIsActive()) {
            // Điều khiển servo1 với nút X (lên/open) và Y (xuống/close) - giữ nguyên
            if (gamepad1.x) {
                robot.servo1.setPosition(Constants.SERVO1_OPEN);  // Lên
            } else if (gamepad1.y) {
                robot.servo1.setPosition(Constants.SERVO1_CLOSE);  // Xuống
            }

            // Điều khiển motor sort với nút B: Quay đến góc tiếp theo (0° -> 120° -> 240° -> 0°)
            if (gamepad1.b && !bPressedLast) {  // Chỉ xử lý khi nhấn mới (tránh spam)
                currentAngleIndex = (currentAngleIndex + 1) % 3;  // Chuyển sang góc tiếp theo
                int targetPosition = targetTicks[currentAngleIndex];

                // Set target và chạy với tốc độ cao
                robot.sortMotor.setTargetPosition(targetPosition);
                robot.sortMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.sortMotor.setPower(1.0);  // Tăng tốc độ để nhanh hơn (có thể thêm constant nếu cần)
            }
            bPressedLast = gamepad1.b;  // Cập nhật trạng thái nút

            // Telemetry chỉ update khi cần (giảm lag) - chỉ khi motor đang chạy hoặc thay đổi
            if (robot.sortMotor.isBusy() || gamepad1.x || gamepad1.y || gamepad1.b) {
                telemetry.addData("Servo1 Position", robot.servo1.getPosition());
                telemetry.addData("Sort Motor Current Position", robot.sortMotor.getCurrentPosition());
                telemetry.addData("Sort Motor Target", robot.sortMotor.getTargetPosition());
                telemetry.addData("Sort Motor Busy", robot.sortMotor.isBusy());
                telemetry.addData("Current Angle Index", currentAngleIndex);
                telemetry.update();
            }

            // Thêm sleep nhỏ để giảm tải CPU (tùy chọn, nếu vẫn chậm)
            sleep(10);
        }

        // Dừng robot
        stopRobot();
    }
}