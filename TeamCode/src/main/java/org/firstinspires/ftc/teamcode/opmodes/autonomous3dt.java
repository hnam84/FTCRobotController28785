package org.firstinspires.ftc.teamcode.opmodes;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.HardwareRobot;

@Autonomous(name = "autonomous3dt")
public class autonomous3dt extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
//(new Pose2d(-61,10, Math.toRadians(270))) th 2
        //
        Pose2d initialPose = new Pose2d(-48, 49, Math.toRadians(-226));


        HardwareRobot.MecanumDrive drive = new HardwareRobot.MecanumDrive(hardwareMap, initialPose);


        Action trajectoryAction = drive.actionBuilder(initialPose)
                .strafeToLinearHeading(new Vector2d(-22, 20), Math.toRadians(-226))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(-11, 20), Math.toRadians(-270))
                .strafeToLinearHeading(new Vector2d(-11, 48), Math.toRadians(-270))
                .strafeToLinearHeading(new Vector2d(2, 57), Math.toRadians(-270))//
                .waitSeconds(2)
                .strafeToLinearHeading(new Vector2d(-22, 20), Math.toRadians(-226))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(12, 20), Math.toRadians(-270))
                .strafeToLinearHeading(new Vector2d(12, 48), Math.toRadians(-270))
                .strafeToLinearHeading(new Vector2d(-22, 20), Math.toRadians(-226))
                .waitSeconds(3)
                .strafeToLinearHeading(new Vector2d(36, 20), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(36, 48), Math.toRadians(90))
                .strafeToLinearHeading(new Vector2d(-22, 20), Math.toRadians(-226))
                .waitSeconds(3)

                .build();

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(trajectoryAction);
    }
}
