package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class HardwareRobot {
    // Khai báo các thiết bị hardware
    public DcMotor leftDriveMotor;   // HEXMotor di chuyển bên trái
    public DcMotor rightDriveMotor;  // HEXMotor di chuyển bên phải
    public DcMotor shooterMotor1;    // HEXMotor bắn bóng 1
    public DcMotor shooterMotor2;    // CORE HEXMotor xoay shooter
    public DcMotor intakeMotor;      // CORE HEXMotor intake
    public Servo servo1;             // Servo 1 (Dùng cho đẩy bóng lên shooter
    public Servo servo2;             // Servo 2 (khai báo tạm chứ chx bt dùng làm gì)
    public Servo servo3;             // Servo 3 (nhìn lên dòng chú thích phía trên)

    // Phương thức khởi tạo hardware
    public void init(HardwareMap hardwareMap) {
        // Ánh xạ thiết bị từ hardware map (tên phải khớp với config trong Driver Station)
        leftDriveMotor = hardwareMap.get(DcMotor.class, "leftmotor");
        rightDriveMotor = hardwareMap.get(DcMotor.class, "rightmotor");
        shooterMotor1 = hardwareMap.get(DcMotor.class, "shooterMotor_1");
        shooterMotor2 = hardwareMap.get(DcMotor.class, "shooterMotor_2");
        intakeMotor = hardwareMap.get(DcMotor.class, "intake_motor");
        servo1 = hardwareMap.get(Servo.class, "servo_1");
        servo2 = hardwareMap.get(Servo.class, "servo_2");
        servo3 = hardwareMap.get(Servo.class, "servo_3");


        // Cài đặt hướng motor (điều chỉnh nếu cần)
        leftDriveMotor.setDirection(DcMotor.Direction.FORWARD);
        rightDriveMotor.setDirection(DcMotor.Direction.REVERSE);
        shooterMotor1.setDirection(DcMotor.Direction.FORWARD);
        shooterMotor2.setDirection(DcMotor.Direction.FORWARD);  // Hoặc REVERSE nếu cần đồng bộ
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);

        // Khởi tạo vị trí servo (tùy chỉnh theo mây đứa sau này muốn chỉnh ntn thì chỉnh)
        servo1.setPosition(0.0);  // đg đóng
        servo2.setPosition(0.0);
        servo3.setPosition(0.0);
    }

    // Phương thức dừng tất cả motor
    public void stop() {
        leftDriveMotor.setPower(0);
        rightDriveMotor.setPower(0);
        shooterMotor1.setPower(0);
        shooterMotor2.setPower(0);
        intakeMotor.setPower(0);
    }
}
