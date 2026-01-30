package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autotestdichuyen")
public class autotestdichuyen extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
//new Pose2d(-61,-10, Math.toRadians(0))
        Pose2d initialPose = new Pose2d(55, 9, Math.toRadians(180)); // o duoi
        // neu o tren 61 9 180

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(50, 9), Math.toRadians(180))
                .strafeToLinearHeading(new Vector2d(61, 9), Math.toRadians(180))
                .strafeToLinearHeading(new Vector2d(63, 9), Math.toRadians(180))
                .strafeToLinearHeading(new Vector2d(50, 9), Math.toRadians(0))
                .strafeToLinearHeading(new Vector2d(63, 9), Math.toRadians(180))


                .waitSeconds(6)
                .build();


        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}

