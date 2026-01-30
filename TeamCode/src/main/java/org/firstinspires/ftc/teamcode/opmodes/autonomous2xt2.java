package org.firstinspires.ftc.teamcode.opmodes;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantFunction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.HardwareRobot;
import org.firstinspires.ftc.teamcode.subsystems.ServoTeleOp;

@Autonomous(name = "autonomous2xt2")
public class autonomous2xt2 extends LinearOpMode {
    private DcMotorEx intakeMotor;
    private DcMotorEx shooterMotor;
    private DcMotorEx coreHex;
    private Servo sortservo;




    public Action sortRotation(double angle) {
        double ticks = angle * (288.0 / 360.0);
        return new Action() {
            private boolean initialized = false;
            @Override
            public boolean run(@NonNull TelemetryPacket packet) {
                if (!initialized) {
                    coreHex.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    coreHex.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                    initialized = true;
                }
                double error = ticks - coreHex.getCurrentPosition();
                if (Math.abs(error) < 3) {
                    coreHex.setPower(0);
                    return false;
                }
                double power = (0.02 * error);
                if (Math.abs(power) < 0.08) power = Math.signum(power) * 0.08;
                coreHex.setPower(Math.max(-0.4, Math.min(0.4, power)));
                return true;
            }
        };
    }
    public Action setServo(double pos) {
        return packet -> {
            sortservo.setPosition(pos);
            return false;
        };
    }

    // Hàm đẩy bóng sử dụng giá trị Up (Open) và Down (Close)
    public Action PushBall(double angle) {
        return new SequentialAction(
                sortRotation(angle),                 // Gọi hàm thay vì new Class
                setServo(Constants.SERVO1_OPEN),
                new SleepAction(0.5),
                setServo(Constants.SERVO1_CLOSE),
                new SleepAction(0.2)
        );
    }
    public Action setIntake(double p) {
        return packet -> {
            intakeMotor.setPower(p);
            return false; // Action tức thời: chạy xong trả về false ngay
        };
    }

    public Action setShooter(double p) {
        return packet -> {
            shooterMotor.setPower(p);
            return false;
        };
    }


    @Override
    public void runOpMode() throws InterruptedException {
        intakeMotor = hardwareMap.get(DcMotorEx.class, "intake");
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");
        coreHex = hardwareMap.get(DcMotorEx.class, "sortMotor");
        sortservo = hardwareMap.get(Servo.class, "servo1");


        Pose2d initialPose = new Pose2d(-48.5, -49, Math.toRadians(222));

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .afterDisp(0, setShooter(0.8))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .stopAndAdd(new SequentialAction(
                        PushBall(113.544),
                        PushBall(113.544),
                        PushBall(113.544),
                        new SleepAction(0.5),
                        setShooter(0)
                ))
                .strafeToLinearHeading(new Vector2d(-11, -22), Math.toRadians(270))
                .afterDisp(0, setIntake(1.0))
                .strafeToLinearHeading(new Vector2d(-11, -55), Math.toRadians(270))
                .stopAndAdd(setIntake(0))
                .afterDisp(0, setShooter(0.8))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .stopAndAdd(new SequentialAction(
                        PushBall(113.544),
                        PushBall(113.544),
                        PushBall(113.544),
                        new SleepAction(0.5),
                        setShooter(0)
                ))
                .strafeToLinearHeading(new Vector2d(12, -22), Math.toRadians(268))
                .afterDisp(0, setIntake(1.0))
                .strafeToLinearHeading(new Vector2d(12, -55), Math.toRadians(268))
                .stopAndAdd(setIntake(0))
                .afterDisp(0, setShooter(0.8))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .stopAndAdd(new SequentialAction(
                        PushBall(113.544),
                        PushBall(113.544),
                        PushBall(113.544),
                        new SleepAction(0.5),
                        setShooter(0)))

                .strafeToLinearHeading(new Vector2d(36, -22), Math.toRadians(-90))
                .afterDisp(0, setIntake(1.0))

                .strafeToLinearHeading(new Vector2d(36, -55), Math.toRadians(-90))
                .stopAndAdd(setIntake(0))
                .afterDisp(0, setShooter(0.8))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .stopAndAdd(new SequentialAction(
                        PushBall(113.544),
                        PushBall(113.544),
                        PushBall(113.544),
                        new SleepAction(0.5),
                        setShooter(0)))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}
