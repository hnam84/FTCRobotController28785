package org.firstinspires.ftc.teamcode.subsystems;


import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;

@TeleOp(name = "SortV2TeleOp", group = "Test")
public class SortV2TeleOp extends LinearOpMode {

    DcMotorEx coreHex;

    // Encoder Core Hex
    static final double TICKS_PER_REV = 288.0;
    static final double TICKS_PER_DEGREE = TICKS_PER_REV / 360.0;

    double kP = 0.02;
    double kI = 0.0;
    double kD = 0.0008;
    double kS = 0.08;   // power tối thiểu thắng ma sát

    double integral = 0;
    double lastError = 0;
    long lastTime = 0;

    boolean rotated = false;

    @Override
    public void runOpMode() {

        coreHex = hardwareMap.get(DcMotorEx.class, "sortMotor");

        coreHex.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        coreHex.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        coreHex.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addLine("Ready - Press B to rotate 120 degrees");
        telemetry.update(); 
        waitForStart();

        while (opModeIsActive()) {

            // Bấm B quay 120 độ (chỉ 1 lần)
            if (gamepad1.b && !rotated) {
                rotateToAnglePID(113.544);
                rotated = true;
            }

            // Reset bằng nút A
            if (gamepad1.a) {
                coreHex.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                coreHex.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                rotated = false;
            }

            telemetry.addData("Encoder", coreHex.getCurrentPosition());
            telemetry.update();

            idle();
        }
    }

    void rotateToAnglePID(double angle) {

        double targetTicks = angle * TICKS_PER_DEGREE;

        integral = 0;
        lastError = 0;
        lastTime = System.nanoTime();

        while (opModeIsActive()) {

            double current = coreHex.getCurrentPosition();
            double error = targetTicks - current;

            long now = System.nanoTime();
            double deltaTime = (now - lastTime) / 1e9;
            lastTime = now;

            integral += error * deltaTime;
            double derivative = (error - lastError) / deltaTime;
            lastError = error;

            double power = (kP * error) + (kI * integral) + (kD * derivative);

            // Minimum power thắng ma sát
            if (Math.abs(power) < kS) {
                power = Math.signum(power) * kS;
            }

            // Giới hạn công suất
            power = Math.max(-0.4, Math.min(0.4, power));
            coreHex.setPower(power);

            telemetry.addData("Target (ticks)", targetTicks);
            telemetry.addData("Current (ticks)", current);
            telemetry.addData("Error", error);
            telemetry.addData("Power", power);
            telemetry.update();

            // Sai số nhỏ thì dừng
            if (Math.abs(error) < 3) break;

            idle();
        }

        coreHex.setPower(0);
    }
}
