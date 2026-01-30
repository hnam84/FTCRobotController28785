package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autonomous4dd")
public class autonomous4dd extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d initialPose = new Pose2d(61, 9, Math.toRadians(180));// o tren
        // neu o duoi 61 -9 180

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-50, 9), Math.toRadians(180))
                .strafeToLinearHeading(new Vector2d(-60, 9), Math.toRadians(90))
                .waitSeconds(6)
                .strafeToLinearHeading(new Vector2d(36, 9), Math.toRadians(90))// xoa neu ko an bong doc
                .strafeToLinearHeading(new Vector2d(36, 47), Math.toRadians(90))// xoa neu ko an bong doc
                .strafeToLinearHeading(new Vector2d(36, 9), Math.toRadians(90))// xoa neu ko an bong doc
                .strafeToLinearHeading(new Vector2d(-60, 9), Math.toRadians(90))// xoa neu ko an bong doc
                .strafeToLinearHeading(new Vector2d(61, -9), Math.toRadians(-90))// xoa neu ko can an bong depo
                .strafeToLinearHeading(new Vector2d(61, -57), Math.toRadians(-90))//xoa neu ko can an bong depo
                .strafeToLinearHeading(new Vector2d(61, -9), Math.toRadians(-90))// xoa neu ko can an bong depo
                .strafeToLinearHeading(new Vector2d(-60, 9), Math.toRadians(90))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}
