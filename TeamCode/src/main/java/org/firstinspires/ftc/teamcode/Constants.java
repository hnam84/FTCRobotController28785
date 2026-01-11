package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;

public class Constants {
    //              TỐC ĐỘ VÀ ĐIỀU KHIỂN CƠ BẢN
    // Tốc độ tối đa cho drive motors (TeleOp)
    public static final double MAX_DRIVE_SPEED = 1.0;

    // Tốc độ cho shooter và intake (TeleOp)
    public static final double SHOOTER_SPEED = 1.0;
    public static final double INTAKE_SPEED = 1.0;

    // Ngưỡng deadzone cho joystick (để tránh rung)
    public static final double JOYSTICK_DEADZONE = 0.1;

    //                  VỊ TRÍ SERVO
    // Vị trí đóng/mở cho các servo (TeleOp)
    public static final double SERVO1_CLOSE = 0.0;  // Đóng servo1
    public static final double SERVO1_OPEN = 1.0;   // Mở servo1
    public static final double SERVO2_CLOSE = 0.0;  // Đóng servo2
    public static final double SERVO2_OPEN = 1.0;   // Mở servo2
    public static final double SERVO3_CLOSE = 0.0;  // Đóng servo3
    public static final double SERVO3_OPEN = 1.0;   // Mở servo3

    //                     PID CHUNG
    // PID chung cho tất cả hệ thống
    public static final double KP = 0.05;  // Proportional gain
    public static final double KI = 0.0;   // Integral gain
    public static final double KD = 0.0;   // Derivative gain

    //            PID RIÊNG CHO TỪNG PHẦN
    // PID cho drive trong TeleOp ( dùng encoder)
    public static final double DRIVE_KP = 0.1;
    public static final double DRIVE_KI = 0.0;
    public static final double DRIVE_KD = 0.0;

    // PID cho shooter (để kiểm soát tốc độ bắn)
    public static final double SHOOTER_KP = 0.05;
    public static final double SHOOTER_KI = 0.0;
    public static final double SHOOTER_KD = 0.0;

    //                   FEEDFORWARD
    // Feedforward chung (KS/KV/KA cho dự đoán lực)
    public static final double KS = 0.1;   // Static feedforward
    public static final double KV = 0.02;  // Velocity feedforward
    public static final double KA = 0.001; // Acceleration feedforward

    //                     THÔNG SỐ CHO AUTONOMOUS
    // Encoder và tính toán khoảng cách
    public static final double MOTOR_CPR = 560;  // chỉ số đo lường số lượng xung/vòng của hexMortor tỉ số truyền 20 : 1
    public static final double WHEEL_DIAMETER_INCHES = 3.937;  // Đường kính bánh xe (inch)
    public static final double INCHES_PER_TICK = (WHEEL_DIAMETER_INCHES * Math.PI) / MOTOR_CPR;  // Tính toán inches per tick

    // PID cho drive trong Auto
    public static final double AUTO_DRIVE_KP = 0.05;
    public static final double AUTO_DRIVE_KI = 0.0;
    public static final double AUTO_DRIVE_KD = 0.0;

    // Tốc độ cho Auto
    public static final double AUTO_DRIVE_SPEED = 0.5;
    public static final double AUTO_TURN_SPEED = 0.3;

    // Khoảng cách mẫu cho Auto
    public static final double SAMPLE_DISTANCE_INCHES = 12.0;

    //                  THÔNG SỐ CHO TANK DRIVE
    // PID cho tank drive (chính xác trong Auto và TeleOp)
    public static final double TANK_KP = 0.05;
    public static final double TANK_KI = 0.0;
    public static final double TANK_KD = 0.0;

    // Feedforward cho tank drive
    public static final double TANK_KS = 0.1;
    public static final double TANK_KV = 0.02;
    public static final double TANK_KA = 0.001;

    // Tốc độ cho tank drive trong Auto
    public static final double TANK_AUTO_SPEED = 0.5;

    // ==================== THÔNG SỐ CHO IMU ====================
    // Tên device IMU trong config (để ánh xạ trong HardwareRobot)
    public static final String IMU_NAME = "imu";  // Tên IMU trong Driver Station config

    // Hướng đặt IMU (orientation) - Thay đổi dựa trên cách đặt IMU trên robot
    // Ví dụ: Nếu IMU đặt với logo REV lên trên và trục X về trước robot, dùng XYZ và RIGHT_HAND
    public static final RevHubOrientationOnRobot.LogoFacingDirection IMU_LOGO_DIRECTION = RevHubOrientationOnRobot.LogoFacingDirection.UP;
    public static final RevHubOrientationOnRobot.UsbFacingDirection IMU_USB_DIRECTION = RevHubOrientationOnRobot.UsbFacingDirection.FORWARD;

    // PID cho turn (quay robot dựa trên IMU, để chính xác trong Auto)
    public static final double TURN_KP = 0.02;  // Proportional gain cho turn (tăng để quay nhanh hơn)
    public static final double TURN_KI = 0.0;   // Integral gain
    public static final double TURN_KD = 0.0;   // Derivative gain

    // Tolerance cho turn (độ sai lệch chấp nhận được, độ)
    public static final double TURN_TOLERANCE_DEGREES = 2.0;

    // Tốc độ tối đa cho turn
    public static final double MAX_TURN_SPEED = 0.5;
}