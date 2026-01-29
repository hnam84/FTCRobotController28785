package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
//import com.qualcomm.robotcore.hardware.DistanceSensor;

public class HardwareRobot {

    // Thêm khai báo 4 động cơ cho mecanum drivetrain
    public DcMotor frontLeft;        //Bánh trước trái
    public DcMotor frontRight;       //Bánh trước phải
    public DcMotor backLeft;         //Bánh sau trái
    public DcMotor backRight;        //Bánh sau phải

    // Các thiết bị khác (giữ nguyên)
    public DcMotor shooterMotor1;    //Motor bắn bóng
    public DcMotor shooterMotor2;    //Core hex xoay phần shooter
    public DcMotor intakeMotor;       //Core hẽ phần intake
    public DcMotor sortMotor;         // Động cơ Core Hex cho phần sort
    public Servo servo1;              //Để tạm cho phần đẩy bóng từ sort lên shooter
    public Servo servo2;              //Khai báo tạm chứ chx biết làm gì
    public Servo servo3;              //Vui lòng nhìn dòng chú thích phía trên

    // Phương thức khởi tạo hardware
    public void init(HardwareMap hardwareMap) {

        // Ánh xạ 4 động cơ mecanum mới
        frontLeft = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRight = hardwareMap.get(DcMotor.class, "frontRight");
        backLeft = hardwareMap.get(DcMotor.class, "backLeft");
        backRight = hardwareMap.get(DcMotor.class, "backRight");

        // Ánh xạ thiết bị khác (giữ nguyên)
        shooterMotor1 = hardwareMap.get(DcMotor.class, "shooterMotor_1");
        shooterMotor2 = hardwareMap.get(DcMotor.class, "shooterMotor_2");
        intakeMotor = hardwareMap.get(DcMotor.class, "intakeMotor");
        sortMotor = hardwareMap.get(DcMotor.class, "sortMotor");  // Thêm motor sort
        servo1 = hardwareMap.get(Servo.class, "servo_1");
        servo2 = hardwareMap.get(Servo.class, "servo_2");
        servo3 = hardwareMap.get(Servo.class, "servo_3");

        // Cài đặt hướng cho mecanum (quan trọng: frontLeft và backLeft REVERSE để di chuyển đúng hướng)
        frontLeft.setDirection(DcMotor.Direction.REVERSE);
        backLeft.setDirection(DcMotor.Direction.REVERSE);
        frontRight.setDirection(DcMotor.Direction.FORWARD);
        backRight.setDirection(DcMotor.Direction.FORWARD);

        // Cài đặt hướng cho motor khác (giữ nguyên)
        shooterMotor1.setDirection(DcMotor.Direction.FORWARD);
        shooterMotor2.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        sortMotor.setDirection(DcMotor.Direction.FORWARD);  // Hướng cho sortMotor

        // Đặt chế độ brake cho tất cả động cơ drivetrain (cả cũ và mới)
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        sortMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);  // Brake cho sortMotor

        // Đặt chế độ encoder cho sortMotor và reset về 0
        sortMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        sortMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Khởi tạo vị trí servo (giữ nguyên)
        servo1.setPosition(Constants.SERVO1_CLOSE);
        servo2.setPosition(Constants.SERVO1_CLOSE);
        servo3.setPosition(Constants.SERVO1_CLOSE);
    }

    // Phương thức dừng tất cả motor (cập nhật để bao gồm mecanum và sortMotor)
    public void stop() {
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
        sortMotor.setPower(0);  // Dừng sortMotor
        shooterMotor1.setPower(0);
        shooterMotor2.setPower(0);
        intakeMotor.setPower(0);
    }

    // Phương thức mới: Đặt tốc độ cho 4 bánh mecanum
    public void setMecanumPower(double frontLeftPower, double frontRightPower, double backLeftPower, double backRightPower) {
        frontLeft.setPower(frontLeftPower);
        frontRight.setPower(frontRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);
    }
}