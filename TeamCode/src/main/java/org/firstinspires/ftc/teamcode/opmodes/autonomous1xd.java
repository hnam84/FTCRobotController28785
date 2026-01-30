package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autonomous1xd")
public class autonomous1xd extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
//new Pose2d(-61,-10, Math.toRadians(0))
        Pose2d initialPose = new Pose2d(61, -9, Math.toRadians(180));

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-61, -9), Math.toRadians(-90))
                .waitSeconds(6)
                .strafeToLinearHeading(new Vector2d(61, -9), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(61, -55), Math.toRadians(-90))
                .waitSeconds(5)
                .strafeToLinearHeading(new Vector2d(61, -9), Math.toRadians(-90))
                .strafeToLinearHeading(new Vector2d(-61, -9), Math.toRadians(-90))
                .waitSeconds(6)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}

