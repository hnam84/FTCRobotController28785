package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autonomous4xd")
public class autonomous4dd1 extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {

        Pose2d initialPose = new Pose2d(61, -9, Math.toRadians(180));// o tren
        // neu o duoi 61 -9 180

        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);

        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(61, -50), Math.toRadians(180))
                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}