package org.firstinspires.ftc.teamcode;

public class VisionTarget {

    public enum TargetType {
        GOAL,
        BALL
    }

    public TargetType type;
    public String color;
    public double centerX;
    public double centerY;
    public double area;
}
