package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autonomous2x")
public class autonomous2x extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
//new Pose2d(-61,-10, Math.toRadians(0))
        Pose2d initialPose = new Pose2d(-48.5, -49, Math.toRadians(222));

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .waitSeconds(3)
                .turn(Math.toRadians(47))
                .lineToY(-55)
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(12, -10), Math.toRadians(268))
                .strafeToLinearHeading(new Vector2d(12, -55), Math.toRadians(268))
                .strafeToLinearHeading(new Vector2d(-22, -22), Math.toRadians(222))
                .waitSeconds(3)
                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}
