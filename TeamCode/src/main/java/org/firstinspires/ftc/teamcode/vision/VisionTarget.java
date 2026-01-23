package org.firstinspires.ftc.teamcode.vision;


public class VisionTarget {

    public enum TargetType {
        GOAL
    }

    public enum TargetColor {
        BLUE,
        PURPLE,
        UNKNOWN
    }

    public TargetType type;
    public TargetColor color;

    public double centerX;
    public double centerY;
    public double area;

    public int id;

    public VisionTarget(
            TargetType type,
            TargetColor color,
            double centerX,
            double centerY,
            double area,
            int id
    ) {
        this.type = type;
        this.color = color;
        this.centerX = centerX;
        this.centerY = centerY;
        this.area = area;
        this.id = id;
    }
}
