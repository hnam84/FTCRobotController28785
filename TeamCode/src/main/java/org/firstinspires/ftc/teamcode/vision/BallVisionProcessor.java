package org.firstinspires.ftc.teamcode.vision;

import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;

/*
 * TASK 2.3
 * - Nhận diện bóng
 * - Xác định màu bóng (BLUE / PURPLE)
 * - Lấy bóng có diện tích lớn nhất
 *
 * Pipeline:
 * RGB -> HSV
 * HSV -> Mask BLUE & PURPLE
 * Mask -> Morphology (lọc nhiễu)
 * Mask -> Contour
 * Contour -> Area + Moments
 * Chọn bóng lớn nhất
 */
public class BallVisionProcessor implements VisionProcessor {

    // ===== KẾT QUẢ CUỐI =====
    private double centerX = -1;
    private double centerY = -1;
    private double area = 0;
    private String color = "UNKNOWN";

    // ===== HSV RANGE (CẦN TUNE THEO SÂN) =====
    // BLUE BALL
    private static final Scalar BLUE_LOW  = new Scalar(85,  80,  50);
    private static final Scalar BLUE_HIGH = new Scalar(125, 255, 255);

    // PURPLE BALL
    private static final Scalar PURPLE_LOW  = new Scalar(140, 60,  50);
    private static final Scalar PURPLE_HIGH = new Scalar(180, 255, 255);

    // ===== FILTER =====
    private static final double MIN_AREA = 500; // lọc nhiễu nhỏ

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        // Không cần dùng calibration
    }

    @Override
    public Object processFrame(Mat frame, long captureTimeNanos) {

        // Reset mỗi frame
        area = 0;
        color = "UNKNOWN";
        centerX = -1;
        centerY = -1;

        // ===== RGB -> HSV =====
        Mat hsv = new Mat();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        // ===== MASK =====
        Mat blueMask = new Mat();
        Mat purpleMask = new Mat();

        Core.inRange(hsv, BLUE_LOW, BLUE_HIGH, blueMask);
        Core.inRange(hsv, PURPLE_LOW, PURPLE_HIGH, purpleMask);

        // ===== MORPHOLOGY (LỌC NHIỄU) =====
        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_ELLIPSE, new Size(5, 5));

        Imgproc.morphologyEx(blueMask, blueMask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(purpleMask, purpleMask, Imgproc.MORPH_OPEN, kernel);

        // ===== BLUE CONTOUR =====
        double blueArea = 0;
        Point blueCenter = null;

        List<MatOfPoint> blueContours = new ArrayList<>();
        Imgproc.findContours(
                blueMask, blueContours, new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        for (MatOfPoint c : blueContours) {
            double a = Imgproc.contourArea(c);
            if (a > blueArea) {
                Moments m = Imgproc.moments(c);
                if (m.m00 != 0) {
                    blueArea = a;
                    blueCenter = new Point(
                            m.m10 / m.m00,
                            m.m01 / m.m00
                    );
                }
            }
        }

        // ===== PURPLE CONTOUR =====
        double purpleArea = 0;
        Point purpleCenter = null;

        List<MatOfPoint> purpleContours = new ArrayList<>();
        Imgproc.findContours(
                purpleMask, purpleContours, new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        for (MatOfPoint c : purpleContours) {
            double a = Imgproc.contourArea(c);
            if (a > purpleArea) {
                Moments m = Imgproc.moments(c);
                if (m.m00 != 0) {
                    purpleArea = a;
                    purpleCenter = new Point(
                            m.m10 / m.m00,
                            m.m01 / m.m00
                    );
                }
            }
        }

        // ===== CHỌN BÓNG LỚN NHẤT =====
        if (blueArea > purpleArea && blueArea > MIN_AREA && blueCenter != null) {
            area = blueArea;
            centerX = blueCenter.x;
            centerY = blueCenter.y;
            color = "BLUE";
        }
        else if (purpleArea > MIN_AREA && purpleCenter != null) {
            area = purpleArea;
            centerX = purpleCenter.x;
            centerY = purpleCenter.y;
            color = "PURPLE";
        }

        // ===== RELEASE =====
        hsv.release();
        blueMask.release();
        purpleMask.release();
        kernel.release();

        return null;
    }

    @Override
    public void onDrawFrame(
            Canvas canvas,
            int onscreenWidth,
            int onscreenHeight,
            float scaleBmpPxToCanvasPx,
            float scaleCanvasDensity,
            Object userContext
    ) {
        // Có thể vẽ crosshair / text debug sau
    }

    // ===== GETTER DÙNG TRONG OPMODE =====
    public boolean hasBall() {
        return area > 0;
    }

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public double getArea() {
        return area;
    }

    public String getColor() {
        return color;
    }
}
