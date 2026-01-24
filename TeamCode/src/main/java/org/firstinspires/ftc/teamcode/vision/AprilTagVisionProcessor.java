package org.firstinspires.ftc.teamcode.vision;

import org.firstinspires.ftc.vision.apriltag.*;
import java.util.ArrayList;
import java.util.List;


public class AprilTagVisionProcessor {

    private final AprilTagProcessor processor;
    private final List<VisionTarget> detectedGoals = new ArrayList<>();

    public AprilTagVisionProcessor() {
        processor = new AprilTagProcessor.Builder()
                .setDrawTagOutline(true)
                .build();
    }

    public AprilTagProcessor getProcessor() {
        return processor;
    }

    public List<VisionTarget> getDetectedGoals() {

        detectedGoals.clear();

        for (AprilTagDetection d : processor.getDetections()) {

            VisionTarget.TargetColor color =
                    (d.id == 20) ? VisionTarget.TargetColor.BLUE :
                            (d.id == 24) ? VisionTarget.TargetColor.PURPLE :
                                    VisionTarget.TargetColor.UNKNOWN;

            detectedGoals.add(
                    new VisionTarget(
                            VisionTarget.TargetType.GOAL,
                            color,
                            d.center.x,
                            d.center.y,
                            0,
                            d.id
                    )
            );
        }

        return detectedGoals;
    }
}