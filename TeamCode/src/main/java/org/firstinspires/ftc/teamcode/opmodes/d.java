package org.firstinspires.ftc.teamcode.opmodes;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.AngularVelConstraint;
import com.acmerobotics.roadrunner.MinVelConstraint;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.TranslationalVelConstraint;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.VelConstraint;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.HardwareRobot;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Autonomous(name = "auto4")
public class d extends LinearOpMode {
    private DcMotor intakeMotor, shooterMotor1, shooterMotor2;
    private Servo servoPush, servoSort;
    private HardwareRobot.MecanumDrive drive;

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTagProcessor;
    private int detectedTagId = -1;

    @Override
    public void runOpMode() throws InterruptedException {
        initHardware();
        initVision();

        // Tốc độ cực chậm để "nhích" vào lấy từng bóng
        VelConstraint crawlVel = new MinVelConstraint(Arrays.asList(
                new TranslationalVelConstraint(10.0),
                new AngularVelConstraint(Math.toRadians(40))
        ));

        // Tọa độ bắt đầu
        Pose2d initialPose = new Pose2d(-48.5, -49, Math.toRadians(222));
        drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        // --- BƯỚC 1: QUÉT TAG TRONG KHI CHỜ (INIT LOOP) ---
        while (!isStarted() && !isStopRequested()) {
            List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
            for (AprilTagDetection d : detections) {
                if (d.id >= 21 && d.id <= 23) detectedTagId = d.id;
            }
            telemetry.addData("STATUS", "READY - SCANNING TAG...");
            telemetry.addData("DETECTED TAG", detectedTagId != -1 ? detectedTagId : "NONE (DEFAULT 21)");
            telemetry.update();
            sleep(20);
        }

        waitForStart();
        if (isStopRequested()) return;

        // --- BƯỚC 2: XÂY DỰNG QUỸ ĐẠO TỔNG THỂ 12 QUẢ ---
        Action fullRoutine = drive.actionBuilder(initialPose)
                // --- CHU KỲ 1: BẮN 3 QUẢ PRELOADS (DỰA TRÊN TAG) ---
                .afterDisp(0, setShooter(Constants.NEAR_SHOOTER_SPEED))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(160)) // Quay mặt nhìn Tag lần cuối
                .waitSeconds(0.8)
                .stopAndAdd(packet -> {
                    List<AprilTagDetection> detections = aprilTagProcessor.getDetections();
                    for (AprilTagDetection d : detections) {
                        if (d.id >= 21 && d.id <= 23) detectedTagId = d.id;
                    }
                    return false;
                })
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222)) // Quay lại hướng rổ
                .waitSeconds(0.4)
                .stopAndAdd(new Action() {
                    Action shootAction;
                    @Override
                    public boolean run(@NonNull TelemetryPacket packet) {
                        if (shootAction == null) {
                            String[] order = getShootingOrder(detectedTagId);
                            shootAction = executeSmartShooting(order);
                        }
                        return shootAction.run(packet);
                    }
                })
                .stopAndAdd(setShooter(0))

                // --- CHU KỲ 2: THU HOẠCH 3 BÓNG CẬN (Bóng 4, 5, 6) ---
                // Thu hoạch bóng 1
                .strafeToLinearHeading(new Vector2d(-11, -40), Math.toRadians(270))
                .stopAndAdd(safeCollectAction(-11, -55, 270, Constants.SERVO2_POSITION1_INTAKE, crawlVel))
                // Thu hoạch bóng 2
                .strafeToLinearHeading(new Vector2d(12, -40), Math.toRadians(268))
                .stopAndAdd(safeCollectAction(12, -55, 268, Constants.SERVO2_POSITION2_INTAKE, crawlVel))
                // Thu hoạch bóng 3
                .strafeToLinearHeading(new Vector2d(36, -40), Math.toRadians(-90))
                .stopAndAdd(safeCollectAction(36, -55, -90, Constants.SERVO2_POSITION3_INTAKE, crawlVel))

                // Quay về bắn lượt 2 (Tổng 6 quả)
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .afterDisp(0, setShooter(0.85))
                .waitSeconds(1.0)
                .stopAndAdd(fireThreeBallsTumu())
                .stopAndAdd(setShooter(0))

                // --- CHU KỲ 3 & 4: CÓ THỂ ĐI HÚT TIẾP HOẶC LẶP LẠI ---
                // (Để đạt 12 quả bạn cần đi hút thêm 2 lượt nữa hoặc tùy chỉnh tọa độ bãi bóng tiếp theo)
                // Ở đây tôi viết mẫu quay lại hút bóng 1-2 lần nữa để đủ số lượng
                .strafeToLinearHeading(new Vector2d(-11, -40), Math.toRadians(270))
                .stopAndAdd(safeCollectAction(-11, -55, 270, Constants.SERVO2_POSITION1_INTAKE, crawlVel))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .afterDisp(0, setShooter(0.85))
                .stopAndAdd(safeSingleShot(Constants.SERVO2_POSITION1_SHOOTER))
                .stopAndAdd(setShooter(0))

                .build();

        Actions.runBlocking(fullRoutine);
        visionPortal.close();
    }

    // --- HÀM HỖ TRỢ ACTION ---

    private Action safeCollectAction(double tx, double ty, double th, double sortPos, VelConstraint vel) {
        return new SequentialAction(
                packet -> { servoSort.setPosition(sortPos); return false; },
                new SleepAction(0.4),
                packet -> { intakeMotor.setPower(Constants.INTAKE_SPEED); return false; },
                drive.actionBuilder(drive.localizer.getPose())
                        .strafeToLinearHeading(new Vector2d(tx, ty), Math.toRadians(th), vel)
                        .build(),
                new SleepAction(0.7),
                packet -> { intakeMotor.setPower(0); return false; },
                drive.actionBuilder(new Pose2d(tx, ty, Math.toRadians(th)))
                        .strafeTo(new Vector2d(tx, ty + 6)) // Lùi lại
                        .build()
        );
    }

    private Action fireThreeBallsTumu() {
        return new SequentialAction(
                safeSingleShot(Constants.SERVO2_POSITION1_SHOOTER),
                safeSingleShot(Constants.SERVO2_POSITION2_SHOOTER),
                safeSingleShot(Constants.SERVO2_POSITION3_SHOOTER)
        );
    }

    public Action safeSingleShot(double pos) {
        return new SequentialAction(
                packet -> { servoSort.setPosition(pos); return false; },
                new SleepAction(0.6),
                packet -> { servoPush.setPosition(Constants.SERVO1_OPEN); return false; },
                new SleepAction(0.5),
                packet -> { servoPush.setPosition(Constants.SERVO1_CLOSE); return false; },
                new SleepAction(0.3)
        );
    }

    private Action executeSmartShooting(String[] order) {
        List<Action> shots = new ArrayList<>();
        for (String color : order) {
            final String c = color;
            shots.add(new SequentialAction(
                    packet -> { servoSort.setPosition(getServoPosByColor(c)); return false; },
                    new SleepAction(0.6),
                    packet -> { servoPush.setPosition(Constants.SERVO1_OPEN); return false; },
                    new SleepAction(0.4),
                    packet -> { servoPush.setPosition(Constants.SERVO1_CLOSE); return false; },
                    new SleepAction(0.3)
            ));
        }
        return new SequentialAction(shots.toArray(new Action[0]));
    }

    private void initHardware() {
        shooterMotor1 = hardwareMap.get(DcMotor.class, "shooter_motor_1");
        shooterMotor2 = hardwareMap.get(DcMotor.class, "shooter_motor_2");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        servoSort = hardwareMap.get(Servo.class, "servo_2");
        servoPush = hardwareMap.get(Servo.class, "servo_1");
        shooterMotor1.setDirection(DcMotor.Direction.REVERSE);
        servoPush.setPosition(Constants.SERVO1_CLOSE);
    }

    private void initVision() {
        aprilTagProcessor = new AprilTagProcessor.Builder().build();
        visionPortal = new VisionPortal.Builder()
                .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))
                .addProcessor(aprilTagProcessor)
                .build();
    }

    private Action setShooter(double p) {
        return packet -> {
            shooterMotor1.setPower(p);
            shooterMotor2.setPower(p);
            return false;
        };
    }

    private String[] getShootingOrder(int id) {
        if (id == 21) return new String[]{"GREEN", "PURPLE1", "PURPLE2"};
        if (id == 22) return new String[]{"PURPLE1", "GREEN", "PURPLE2"};
        if (id == 23) return new String[]{"PURPLE1", "PURPLE2", "GREEN"};
        return new String[]{"GREEN", "PURPLE1", "PURPLE2"};
    }

    private double getServoPosByColor(String color) {
        switch (color) {
            case "PURPLE2": return Constants.SERVO2_POSITION3_SHOOTER;
            case "PURPLE1": return Constants.SERVO2_POSITION2_SHOOTER;
            default: return Constants.SERVO2_POSITION1_SHOOTER;
        }
    }
}