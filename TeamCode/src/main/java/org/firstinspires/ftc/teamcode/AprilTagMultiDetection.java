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

@Autonomous(name = "AprilTagMultiDetection", group = "Linear OpMode")
public class AprilTagMultiDetection extends BaseOpMode {

    // ===== VISION =====
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;

    private static final int[] TARGET_TAG_IDS = {20, 21, 22, 23, 24};  // Thêm 25 nếu cần

    @Override
    public void runOpMode() throws InterruptedException {

        // ===== INIT ROBOT =====
        initRobot();

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

        // ===== MAIN LOOP =====
        while (opModeIsActive()) {

            telemetry.clear();

            // ===== APRILTAG & SCENARIO LOGIC =====
            List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

            if (!detections.isEmpty()) {
                for (AprilTagDetection d : detections) {
                    if (isTargetTag(d.id)) {
                        // 1. Hiển thị thông tin cơ bản
                        telemetry.addData("ID Detect", d.id);
                        telemetry.addData("Pos (in)", "%.1f", d.ftcPose.range);

                        // 2. XỬ LÝ KỊCH BẢN BẮN BÓNG (LOGIC MỚI THÊM)
                        String[] shootingOrder = null;

                        switch (d.id) {
                            case 21:
                                // Xanh -> Tím -> Tím
                                shootingOrder = new String[]{"BLUE", "PURPLE", "PURPLE"};
                                telemetry.addData(">> STRATEGY", "CASE 21 (Blue Start)");
                                break;
                            case 22:
                                // Tím -> Xanh -> Tím
                                shootingOrder = new String[]{"PURPLE", "BLUE", "PURPLE"};
                                telemetry.addData(">> STRATEGY", "CASE 22 (Purple Start)");
                                break;
                            case 23:
                                // Tím -> Tím -> Xanh
                                shootingOrder = new String[]{"PURPLE", "PURPLE", "BLUE"};
                                telemetry.addData(">> GOAL", "CASE 23 (Double Purple)");
                                break;
                            case 24:
                                telemetry.addData(">> GOAL", "BLUE");
                                break;
                            case 25:
                                telemetry.addData(">> GOAL", "RED");
                                break;
                            // Thêm case 20 nếu cần
                        }

                        // 3. Hiển thị danh sách bóng cần bắn
                        if (shootingOrder != null) {
                            telemetry.addLine("--- SHOOTING ORDER ---");
                            for (int i = 0; i < shootingOrder.length; i++) {
                                telemetry.addData("   Shot #" + (i + 1), shootingOrder[i]);
                            }
                            telemetry.addLine("----------------------");
                        }
                    }
                }
            } else {
                telemetry.addData("AprilTag", "Searching...");
            }

            telemetry.update();
            sleep(20);  // Tăng từ 10ms để ổn định hơn
        }

        // ===== STOP =====
        visionPortal.close();
    }

    // ===== UTIL =====
    private boolean isTargetTag(int id) {
        for (int targetId : TARGET_TAG_IDS) {
            if (id == targetId) return true;
        }
        return false;  // Đã thêm dấu chấm phẩy
    }
}