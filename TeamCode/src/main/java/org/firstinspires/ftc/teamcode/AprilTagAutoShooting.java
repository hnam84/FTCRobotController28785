package org.firstinspires.ftc.teamcode;


import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Autonomous(name = "AprilTagAutoShooting", group = "Linear OpMode")
public class AprilTagAutoShooting extends BaseOpMode {

    // ===== VISION =====
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;

    private static final int[] TARGET_TAG_IDS = {20, 21, 22, 23, 24};  // Bao gồm 20 nếu cần

    @Override
    public void runOpMode() throws InterruptedException {

        // ===== INIT ROBOT =====
        initRobot();

        // ===== INIT SHOOTER VÀ SERVO =====
        // Bật shooter tốc độ NEAR_SHOOTER_SPEED
        robot.shooterMotor1.setPower(Constants.NEAR_SHOOTER_SPEED);
        robot.shooterMotor2.setPower(Constants.NEAR_SHOOTER_SPEED);

        // Servo2 mặc định ở góc POSITION1_SHOOTER (cho green)
        robot.servo2.setPosition(Constants.SERVO2_POSITION1_SHOOTER);

        // Servo1 đóng
        robot.servo1.setPosition(Constants.SERVO1_CLOSE);

        // ===== PROCESSORS =====
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagOutline(true)
                .build();

        // ===== CAMERA =====
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(aprilTagProcessor)
                .build();

        // ===== CHỜ CAMERA READY =====
        while (!isStopRequested() && visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            sleep(20);
        }

        // ===== LOCK CAMERA =====
        ExposureControl exposure = visionPortal.getCameraControl(ExposureControl.class);
        GainControl gain = visionPortal.getCameraControl(GainControl.class);

        if (exposure != null && exposure.isExposureSupported()) {
            exposure.setMode(ExposureControl.Mode.Manual);
            exposure.setExposure(15, TimeUnit.MILLISECONDS);
        }

        if (gain != null) {
            gain.setGain(50);
        }

        // ===== WAIT START =====
        waitForStartWithTelemetry();

        // ===== CHỜ PHÁT HIỆN APRILTAG =====
        boolean tagDetected = false;
        while (opModeIsActive() && !tagDetected) {
            telemetry.clear();
            telemetry.addData("Status", "Searching for AprilTag...");

            List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

            if (!detections.isEmpty()) {
                for (AprilTagDetection d : detections) {
                    if (isTargetTag(d.id)) {
                        // Phát hiện tag hợp lệ
                        telemetry.addData("ID Detect", d.id);
                        telemetry.addData("Pos (in)", "%.1f", d.ftcPose.range);

                        // Lấy shooting order
                        String[] shootingOrder = getShootingOrder(d.id);

                        if (shootingOrder != null) {
                            telemetry.addData(">> STRATEGY", "Executing shooting sequence for ID " + d.id);
                            telemetry.addLine("--- SHOOTING ORDER ---");
                            for (int i = 0; i < shootingOrder.length; i++) {
                                telemetry.addData("   Shot #" + (i + 1), shootingOrder[i]);
                            }
                            telemetry.addLine("----------------------");
                            telemetry.update();

                            // Thực hiện sequence bắn
                            executeShootingSequence(shootingOrder);

                            // Dừng shooter sau sequence
                            robot.shooterMotor1.setPower(0);
                            robot.shooterMotor2.setPower(0);
                            telemetry.addData("Status", "Shooting sequence completed. Shooter stopped.");
                            telemetry.update();

                            tagDetected = true;
                        }
                        break; // Chỉ xử lý tag đầu tiên
                    }
                }
            }

            if (!tagDetected) {
                telemetry.update();
                sleep(20);
            }
        }

        // ===== STOP =====
        visionPortal.close();
        stopRobot();  // Dừng tất cả motor
    }

    // ===== UTIL =====
    private boolean isTargetTag(int id) {
        for (int targetId : TARGET_TAG_IDS) {
            if (id == targetId) return true;
        }
        return false;
    }

    // Lấy shooting order
    private String[] getShootingOrder(int id) {
        switch (id) {
            case 21:
                return new String[]{"GREEN", "PURPLE1", "PURPLE2"};  // Xanh -> Tím -> Tím
            case 22:
                return new String[]{"PURPLE1", "GREEN", "PURPLE2"};  // Tím -> Xanh -> Tím
            case 23:
                return new String[]{"PURPLE1", "PURPLE2", "GREEN"};  // Tím -> Tím -> Xanh
            case 24:
                return new String[]{"BLUE"};  // Goal BLUE (giữ nguyên nếu cần)
            case 25:
                return new String[]{"RED"};   // Goal RED (giữ nguyên nếu cần)
            default:
                return null;
        }
    }

    // Thực hiện sequence bắn
    private void executeShootingSequence(String[] order) throws InterruptedException {
        for (String color : order) {
            // Quay servo2 đến góc tương ứng
            double servo2Position = getServo2PositionForColor(color);
            robot.servo2.setPosition(servo2Position);
            telemetry.addData("Servo2", "Moving to position for " + color);
            telemetry.update();
            sleep(500);  // Chờ servo quay

            // Mở servo1
            robot.servo1.setPosition(Constants.SERVO1_OPEN);
            telemetry.addData("Servo1", "Open for shot");
            telemetry.update();
            sleep(500);  // Chờ bắn

            // Đóng servo1
            robot.servo1.setPosition(Constants.SERVO1_CLOSE);
            telemetry.addData("Servo1", "Closed");
            telemetry.update();
            sleep(1000);  // Chờ giữa các bóng
        }
    }

    // Ánh xạ màu sang góc servo2
    private double getServo2PositionForColor(String color) {
        switch (color) {
            case "PURPLE2":
                return Constants.SERVO2_POSITION3_SHOOTER;
            case "PURPLE1":
                return Constants.SERVO2_POSITION2_SHOOTER;  // Dùng góc 2 cho purple; nếu cần góc 3 cho purple thứ 2, sửa logic
            case "GREEN":
                return Constants.SERVO2_POSITION1_SHOOTER;  // Mặc định
        }
        return
                0;
    }
}