package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autonomous2xt")
public class autonomous2xt extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        Pose2d initialPose = new Pose2d(-49, -50, Math.toRadians(230));

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(-11, -22), Math.toRadians(270))
                .strafeToLinearHeading(new Vector2d(-11, -47), Math.toRadians(270))
                .strafeToLinearHeading(new Vector2d(0, -56), Math.toRadians(270))
                .waitSeconds(2)
                .strafeToLinearHeading(new Vector2d(-23, -21), Math.toRadians(222))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(12, -22), Math.toRadians(270))
                .strafeToLinearHeading(new Vector2d(12, -47), Math.toRadians(270))//
                .waitSeconds(2)
                .strafeToLinearHeading(new Vector2d(-24, -20), Math.toRadians(222))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(36, -22), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(36, -47), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(-25, -19), Math.toRadians(222))
                .waitSeconds(3)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}
