package org.firstinspires.ftc.teamcode.opmodes;

public class PIDFController {

    private double kP, kI, kD, kV;

    private double integral = 0;
    private double lastError = 0;

    public PIDFController(double kP, double kI, double kD, double kV) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
        this.kV = kV;
    }


    public double calculate(double target, double current) {
        double error = target - current;

        integral += error;
        double derivative = error - lastError;
        lastError = error;

        return (kP * error) + (kI * integral) + (kD * derivative);
    }

    /**
     * Feedforward theo vận tốc mong muốn
     * @param velocity thường = 1 hoặc -1
     */
    public double feedForward(double velocity) {
        return kV * velocity;
    }

    /** Reset PID khi sang bước mới */
    public void reset() {
        integral = 0;
        lastError = 0;
    }

    /** Đổi PID nhanh khi tune */
    public void setPID(double kP, double kI, double kD) {
        this.kP = kP;
        this.kI = kI;
        this.kD = kD;
    }

    public void setKV(double kV) {
        this.kV = kV;
    }
}