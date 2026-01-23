package org.firstinspires.ftc.teamcode;

import android.util.Size;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.teamcode.vision.BallVisionProcessor;

@Autonomous(name = "AprilTagMultiDetection", group = "Linear OpMode")
public class AprilTagMultiDetection extends BaseOpMode {

    // ===== Vision =====
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private BallVisionProcessor ballProcessor;

    // AprilTag IDs quan tâm
    private static final int[] TARGET_TAG_IDS = {20, 21, 22, 23, 24};

    @Override
    public void runOpMode() throws InterruptedException {

        // ===== INIT ROBOT =====
        initRobot();
           
        // ===== INIT PROCESSORS (CHỈ 1 LẦN) =====
        aprilTagProcessor = new AprilTagProcessor.Builder()
                .setDrawTagOutline(true)
                .build();

        ballProcessor = new BallVisionProcessor();

        // ===== INIT CAMERA + VISION PORTAL =====
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(aprilTagProcessor) // AprilTag
                .addProcessor(ballProcessor)     // Ball + Color
                .build();

        // ===== WAIT START =====
        waitForStartWithTelemetry();

        // ===== MAIN LOOP =====
        while (opModeIsActive()) {

            // ===== APRILTAG DETECTION =====
            java.util.List<AprilTagDetection> detections =
                    aprilTagProcessor.getDetections();

            if (!detections.isEmpty()) {
                for (AprilTagDetection detection : detections) {

                    int tagID = detection.id;

                    if (isTargetTag(tagID)) {

                        telemetry.addData("AprilTag ID", tagID);
                        telemetry.addData("Tag Type", getTagType(tagID));
                        telemetry.addData(
                                "Tag Center (px)",
                                "(%.1f, %.1f)",
                                detection.center.x,
                                detection.center.y
                        );
                        telemetry.addData(
                                "Range (in)",
                                "%.1f",
                                detection.ftcPose.range
                        );
                        telemetry.addData(
                                "Bearing (deg)",
                                "%.1f",
                                detection.ftcPose.bearing
                        );
                    }
                }
            } else {
                telemetry.addData("AprilTag", "None");
            }

            // ===== BALL DETECTION =====
            if (ballProcessor.hasBall()) {
                telemetry.addData("Ball", "FOUND");
                telemetry.addData("Ball Color", ballProcessor.getColor());
                telemetry.addData("Ball Area", "%.0f", ballProcessor.getArea());
                telemetry.addData(
                        "Ball Center (px)",
                        "(%.1f, %.1f)",
                        ballProcessor.getCenterX(),
                        ballProcessor.getCenterY()
                );
            } else {
                telemetry.addData("Ball", "NOT FOUND");
            }

            telemetry.update();
            sleep(20);
        }

        // ===== STOP =====
        visionPortal.close();
    }

    // ===== UTIL =====
    private boolean isTargetTag(int id) {
        for (int targetId : TARGET_TAG_IDS) {
            if (id == targetId) return true;
        }
        return false;
    }

    private String getTagType(int id) {
        switch (id) {
            case 20: return "Blue Goal";
            case 24: return "Red Goal";
            case 21: return "Spike Mark 1";
            case 22: return "Spike Mark 2";
            case 23: return "Spike Mark 3";
            default: return "Unknown";
        }
    }
}
