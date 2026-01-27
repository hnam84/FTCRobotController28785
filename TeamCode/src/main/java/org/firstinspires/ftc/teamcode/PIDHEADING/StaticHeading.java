package org.firstinspires.ftc.teamcode.PIDHEADING;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Static Heading Degrees")
public class StaticHeading extends LinearOpMode {
    Drivetrain drivetrain = new Drivetrain();
    ElapsedTime timer = new ElapsedTime();

    private IMU imu;
    private double lastError = 0;
    private double integralSum = 0;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        drivetrain.init(hardwareMap);

        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD);

        imu.initialize(new IMU.Parameters(orientationOnRobot));
        imu.resetYaw();

        // ĐẶT MỤC TIÊU LÀ 90 ĐỘ
        double referenceAngle = 90.0;

        waitForStart();
        timer.reset();

        while(opModeIsActive()){
            // LẤY GÓC HIỆN TẠI BẰNG ĐỘ (DEGREES)
            double currentAngle = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);

            double dt = timer.seconds();
            if (dt > 1e-6) {
                double power = PIDControl(referenceAngle, currentAngle, dt);
                drivetrain.power(power);

                telemetry.addData("Target (Deg)", referenceAngle);
                telemetry.addData("Current (Deg)", currentAngle);
                telemetry.addData("Error (Deg)", referenceAngle - currentAngle);
                telemetry.addData("Power Out", power);
                telemetry.update();
            }
        }
    }

    public double PIDControl(double reference, double state, double dt) {
        // Tính sai số bằng độ
        double error = reference - state;

        // Xử lý Angle Wrap cho đơn vị Độ (-180 đến 180)
        while (error > 180) error -= 360;
        while (error <= -180) error += 360;

        // Lấy hằng số từ Dashboard
        double Kp = PIDConstants.Kp;
        double Ki = PIDConstants.Ki;
        double Kd = PIDConstants.Kd;

        // Tích phân (Integral)
        integralSum += error * dt;

        // Giới hạn tích phân để tránh quay loạn xạ
        if (Math.abs(integralSum) > 10) integralSum = Math.signum(integralSum) * 10;

        // Đạo hàm (Derivative)
        double derivative = (error - lastError) / dt;
        lastError = error;
        timer.reset();

        // Tính toán đầu ra
        double out = (error * Kp) + (derivative * Kd) + (integralSum * Ki);

        // GIẢI QUYẾT VIỆC KẸT Ở Góc: Thêm công suất tối thiểu (Feedforward)
        double minPower = 0.12; // Lực tối thiểu để thắng ma sát bánh Mecanum
        if (Math.abs(error) > 1.0) { // Nếu lệch hơn 1 độ
            out += Math.signum(error) * minPower;
        }

        return out;
    }
}