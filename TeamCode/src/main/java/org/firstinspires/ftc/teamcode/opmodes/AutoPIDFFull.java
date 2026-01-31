package org.firstinspires.ftc.teamcode.opmodes;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@Autonomous(name = "Auto_PIDF_Full", group = "Auto")
public class AutoPIDFFull extends LinearOpMode {

    DcMotorEx lf, lb, rf, rb;
    IMU imu;

    PIDFController movePID = new PIDFController(0.01, 0, 0.001, 0.00057762213960744050);
    PIDFController turnPID = new PIDFController(0.01, 0, 0.001, 0.00057762213960744050);
    // PIDFController movePID = new PIDFController(0.012, 0, 0.001, 0.0006);
    // PIDFController turnPID = new PIDFController(0.02, 0, 0.001, 0.05);

    static final double TICKS_PER_CM = 45;

    @Override
    public void runOpMode() {

        lf = hardwareMap.get(DcMotorEx.class, "frontLeft");
        lb = hardwareMap.get(DcMotorEx.class, "backLeft");
        rf = hardwareMap.get(DcMotorEx.class, "frontRight");
        rb = hardwareMap.get(DcMotorEx.class, "backRight");

        lf.setDirection(DcMotor.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.REVERSE);

        imu = hardwareMap.get(IMU.class, "imu");
        imu.resetYaw();

        resetEncoders();

        waitForStart();

        moveForward(20);   // 1. Đi thẳng 80cm
        turnToAngle(45);   // 2. Xoay 90°
    }

    // =====================

    void moveForward(double cm) {
        double target = cm * TICKS_PER_CM;
        movePID.reset();

        while (opModeIsActive()) {
            double current = avgEncoder();
            double pid = movePID.calculate(target, current);
            double power = pid + movePID.feedForward(1);

            power = clip(power, 0.6);
            setAll(power);

            if (Math.abs(target - current) < 20) break;
        }
        stopAll();
        sleep(200);
    }

    void turnToAngle(double angle) {
        turnPID.reset();

        while (opModeIsActive()) {
            double current = imu.getRobotYawPitchRollAngles()
                    .getYaw(AngleUnit.DEGREES);

            double output = turnPID.calculate(angle, current);
            output = clip(output, 0.5);

            lf.setPower(output);
            lb.setPower(output);
            rf.setPower(-output);
            rb.setPower(-output);

            if (Math.abs(angle - current) < 2) break;
        }
        stopAll();
        sleep(200);
    }

    // =====================

    void resetEncoders() {
        for (DcMotorEx m : new DcMotorEx[]{lf, lb, rf, rb}) {
            m.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            m.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
    }

    double avgEncoder() {
        return (Math.abs(lf.getCurrentPosition())
                + Math.abs(lb.getCurrentPosition())
                + Math.abs(rf.getCurrentPosition())
                + Math.abs(rb.getCurrentPosition())) / 4.0;
    }

    void setAll(double p) {
        // Nếu bánh sau bên phải (backRight) chạy chậm hơn bánh sau bên trái (backLeft),
        // hãy thử tăng giá trị này một chút (ví dụ: 1.05 hoặc 1.1) để bù lại.
        double backRightCorrection = 1.0;

        lf.setPower(p);
        lb.setPower(p);
        rf.setPower(p);
        rb.setPower(p * backRightCorrection);
    }

    void stopAll() {
        setAll(0);
    }

    double clip(double v, double max) {
        return Math.max(-max, Math.min(max, v));
    }
}