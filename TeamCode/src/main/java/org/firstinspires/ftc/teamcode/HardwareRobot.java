package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.DistanceSensor;

public class HardwareRobot {
    // Khai báo các thiết bị hardware
    public DcMotor leftDriveMotor;   //Bánh trái
    public DcMotor rightDriveMotor;  //Bánh phải
    public DcMotor shooterMotor1;    //Motor bắn bóng
    public DcMotor shooterMotor2;    //Core hex xoay phần shooter
    public DcMotor intakeMotor;       //Core hẽ phần intake
    public Servo servo1;              //Để tạm cho phần đẩy bóng từ sort lên shooter
    public Servo servo2;              //Khai báo tạm chứ chx biết làm gì
    public Servo servo3;              //Vui lòng nhìn dòng chú thích phía trên


    // Phương thức khởi tạo hardware
    public void init(HardwareMap hardwareMap) {
        // Ánh xạ thiết bị
        leftDriveMotor = hardwareMap.get(DcMotor.class, "leftmotor");
        rightDriveMotor = hardwareMap.get(DcMotor.class, "rightmotor");
        shooterMotor1 = hardwareMap.get(DcMotor.class, "shootermotor_1");
        shooterMotor2 = hardwareMap.get(DcMotor.class, "shootermotor_2");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        servo1 = hardwareMap.get(Servo.class, "servo_1");
        servo2 = hardwareMap.get(Servo.class, "servo_2");
        servo3 = hardwareMap.get(Servo.class, "servo_3");


        // Cài đặt hướng motor (giữ nguyên)
        leftDriveMotor.setDirection(DcMotor.Direction.FORWARD);
        rightDriveMotor.setDirection(DcMotor.Direction.REVERSE);
        shooterMotor1.setDirection(DcMotor.Direction.FORWARD);
        shooterMotor2.setDirection(DcMotor.Direction.FORWARD);
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);

        // Khởi tạo vị trí servo (sử dụng hằng số từ Constants)
        servo1.setPosition(Constants.SERVO1_CLOSE);
        servo2.setPosition(Constants.SERVO2_CLOSE);
        servo3.setPosition(Constants.SERVO3_CLOSE);
    }

    // Phương thức dừng tất cả motor (giữ nguyên)
    public void stop() {
        leftDriveMotor.setPower(0);
        rightDriveMotor.setPower(0);
        shooterMotor1.setPower(0);
        shooterMotor2.setPower(0);
        intakeMotor.setPower(0);
    }
}