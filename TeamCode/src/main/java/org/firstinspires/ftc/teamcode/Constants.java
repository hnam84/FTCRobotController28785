package org.firstinspires.ftc.teamcode;

public class  {
    // Tốc độ tối đa của robot
    public static final double MAX_DRIVE_SPEED = 1.0;

    // Tốc độ cho shooter và intake
    public static final double SHOOTER_SPEED = 1.0;
    public static final double INTAKE_SPEED = 0.8;

    // Vị trí servo (góc đóng/mở, giá trị từ 0.0 đến 1.0)
    public static final double SERVO1_CLOSE = 0.0;  // Đóng servo1
    public static final double SERVO1_OPEN = 1.0;   // Mở servo1
    public static final double SERVO2_CLOSE = 0.0;  // Đóng servo2
    public static final double SERVO2_OPEN = 1.0;   // Mở servo2
    public static final double SERVO3_CLOSE = 0.0;  // Đóng servo3
    public static final double SERVO3_OPEN = 1.0;   // Mở servo3

    // Thông số PID (cho drive motors  dùng encoder; điều chỉnh dựa trên test)
    public static final double DRIVE_KP = 0.1;  // Proportional gain
    public static final double DRIVE_KI = 0.0;  // Integral gain
    public static final double DRIVE_KD = 0.0;  // Derivative gain

    // Thông số PID cho shooter ( dùng encoder để kiểm soát tốc độ bắn)
    public static final double SHOOTER_KP = 0.05;
    public static final double SHOOTER_KI = 0.0;
    public static final double SHOOTER_KD = 0.0;

    // Ngưỡng deadzone cho joystick
    public static final double JOYSTICK_DEADZONE = 0.1;
}
