package org.firstinspires.ftc.teamcode.opmodes;

import android.util.Size;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.BaseOpMode;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.HardwareRobot;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import java.util.List;

@Autonomous(name = "Auto2xd", group = "Linear OpMode")
public class auto2d extends BaseOpMode {

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private int detectedTagId = -1;

    @Override
    public void runOpMode() throws InterruptedException {
        // 1. KHỞI TẠO
        initRobot();
        initVision();

        // 2. NHẬN DIỆN TRONG KHI CHỜ (INIT LOOP)
        while (!isStarted() && !isStopRequested()) {
            List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
            for (AprilTagDetection d : detections) {
                if (d.id >= 21 && d.id <= 23) {
                    detectedTagId = d.id;
                    telemetry.addData("Status", "AprilTag ID Detected: " + d.id);
                }
            }
            telemetry.addData("Vision", "Waiting for Start...");
            telemetry.update();
        }

        waitForStart();
        if (isStopRequested()) return;

        // 3. CÀI ĐẶT ROADRUNNER
        Pose2d initialPose = new Pose2d(-49, 50, Math.toRadians(-222));
        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        // Lấy thứ tự bắn dựa trên Tag đã quét
        String[] shootingOrder = getShootingOrder(detectedTagId);

        // 4. XÂY DỰNG QUỸ ĐẠO TOÀN DIỆN
        Action autoRoutine = drive.actionBuilder(initialPose)
                // --- BẮN 3 QUẢ ĐẦU (DỰA TRÊN TAG) ---
                .strafeToLinearHeading(new Vector2d(-17, 15), Math.toRadians(-222))
                .stopAndAdd(setShooterAction(Constants.NEAR_SHOOTER_SPEED))
                .waitSeconds(0.5)
                .stopAndAdd(executeSmartShooting(shootingOrder))
                .stopAndAdd(setShooterAction(0))


                .strafeToLinearHeading(new Vector2d(0, 22), Math.toRadians(270))
                .build();

        // CHẠY CHƯƠNG TRÌNH
        Actions.runBlocking(autoRoutine);

        visionPortal.close();
        stopRobot();
    }

    // --- CÁC HÀM ACTIONS HỖ TRỢ ---

    private Action setShooterAction(double p) {
        return packet -> {
            robot.shooterMotor1.setPower(p);
            robot.shooterMotor2.setPower(p);
            return false;
        };
    }

    private Action setIntakeAction(double p) {
        return packet -> {
            robot.intakeMotor.setPower(p);
            return false;
        };
    }

    private Action executeSmartShooting(String[] order) {
        SequentialAction seq = new SequentialAction();
        for (String color : order) {
            seq = new SequentialAction(
                    seq,
                    packet -> { robot.servo2.setPosition(getServo2PositionForColor(color)); return false; },
                    new SleepAction(0.6),
                    packet -> { robot.servo1.setPosition(Constants.SERVO1_OPEN); return false; },
                    new SleepAction(0.5),
                    packet -> { robot.servo1.setPosition(Constants.SERVO1_CLOSE); return false; },
                    new SleepAction(0.3)
            );
        }
        return seq;
    }

    private void initVision() {
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .setCameraResolution(new Size(640, 480))
                .addProcessor(aprilTagProcessor)
                .build();
    }

    private String[] getShootingOrder(int id) {
        if (id == 21) return new String[]{"GREEN", "PURPLE1", "PURPLE2"};
        if (id == 22) return new String[]{"PURPLE1", "GREEN", "PURPLE2"};
        if (id == 23) return new String[]{"PURPLE1", "PURPLE2", "GREEN"};
        return new String[]{"GREEN", "PURPLE1", "PURPLE2"};
    }

    private double getServo2PositionForColor(String color) {
        switch (color) {
            case "PURPLE2": return Constants.SERVO2_POSITION3_SHOOTER;
            case "PURPLE1": return Constants.SERVO2_POSITION2_SHOOTER;
            default: return Constants.SERVO2_POSITION1_SHOOTER;
        }
    }
}