package org.firstinspires.ftc.teamcode.vision;

import android.graphics.Canvas;

import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
 * TASK 2.3 (SỬA ĐỔI)
 * - Nhận diện bóng
 * - Xác định màu bóng (GREEN / PURPLE)
 * - Lấy top 3 bóng có diện tích lớn nhất, sắp xếp từ lớn nhất đến nhỏ nhất
 *
 * Pipeline:
 * RGB -> HSV
 * HSV -> Mask GREEN & PURPLE
 * Mask -> Morphology (lọc nhiễu)
 * Mask -> Contour
 * Contour -> Area + Moments
 * Lưu tất cả bóng hợp lệ, sắp xếp và trả top 3
 */
public class BallVisionProcessor implements VisionProcessor {

    // ===== LỚP BALL ĐỂ LƯU THÔNG TIN =====
    public static class Ball {
        private String color;
        private double area;
        private double centerX, centerY;

        public Ball(String color, double area, double centerX, double centerY) {
            this.color = color;
            this.area = area;
            this.centerX = centerX;
            this.centerY = centerY;
        }

        public String getColor() { return color; }
        public double getArea() { return area; }
        public double getCenterX() { return centerX; }
        public double getCenterY() { return centerY; }
    }

    // ===== DANH SÁCH BÓNG PHÁT HIỆN =====
    private List<Ball> detectedBalls = new ArrayList<>();

    // ===== HSV RANGE (CẦN TUNE THEO SÂN) =====
    // GREEN BALL (thay thế BLUE)
    private static final Scalar GREEN_LOW  = new Scalar(35,  50,  50);  // Tune nếu cần (Hue ~35-80 cho xanh lá)
    private static final Scalar GREEN_HIGH = new Scalar(80, 255, 255);

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

        // Reset danh sách mỗi frame
        detectedBalls.clear();

        // ===== RGB -> HSV =====
        Mat hsv = new Mat();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        // ===== MASK =====
        Mat greenMask = new Mat();
        Mat purpleMask = new Mat();

        Core.inRange(hsv, GREEN_LOW, GREEN_HIGH, greenMask);
        Core.inRange(hsv, PURPLE_LOW, PURPLE_HIGH, purpleMask);

        // ===== MORPHOLOGY (LỌC NHIỄU) =====
        Mat kernel = Imgproc.getStructuringElement(
                Imgproc.MORPH_ELLIPSE, new Size(5, 5));

        Imgproc.morphologyEx(greenMask, greenMask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(purpleMask, purpleMask, Imgproc.MORPH_OPEN, kernel);

        // ===== GREEN CONTOURS =====
        List<MatOfPoint> greenContours = new ArrayList<>();
        Imgproc.findContours(
                greenMask, greenContours, new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        for (MatOfPoint c : greenContours) {
            double a = Imgproc.contourArea(c);
            if (a > MIN_AREA) {
                Moments m = Imgproc.moments(c);
                if (m.m00 != 0) {
                    double cx = m.m10 / m.m00;
                    double cy = m.m01 / m.m00;
                    detectedBalls.add(new Ball("GREEN", a, cx, cy));
                }
            }
        }

        // ===== PURPLE CONTOURS =====
        List<MatOfPoint> purpleContours = new ArrayList<>();
        Imgproc.findContours(
                purpleMask, purpleContours, new Mat(),
                Imgproc.RETR_EXTERNAL,
                Imgproc.CHAIN_APPROX_SIMPLE
        );

        for (MatOfPoint c : purpleContours) {
            double a = Imgproc.contourArea(c);
            if (a > MIN_AREA) {
                Moments m = Imgproc.moments(c);
                if (m.m00 != 0) {
                    double cx = m.m10 / m.m00;
                    double cy = m.m01 / m.m00;
                    detectedBalls.add(new Ball("PURPLE", a, cx, cy));
                }
            }
        }

        // ===== RELEASE =====
        hsv.release();
        greenMask.release();
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

    // ===== PHƯƠNG THỨC MỚI: LẤY TOP BALLS =====
    public List<Ball> getTopBalls(int count) {
        // Sắp xếp theo diện tích giảm dần
        detectedBalls.sort(Comparator.comparingDouble(Ball::getArea).reversed());
        // Trả về top 'count' bóng (hoặc ít hơn nếu không đủ)
        return detectedBalls.subList(0, Math.min(count, detectedBalls.size()));
    }

    // ===== GETTER CŨ (VẪN GIỮ CHO TƯƠNG THÍCH, TRẢ VỀ BÓNG LỚN NHẤT) =====
    public boolean hasBall() {
        return !detectedBalls.isEmpty();
    }

    public double getCenterX() {
        return detectedBalls.isEmpty() ? -1 : detectedBalls.get(0).getCenterX();
    }

    public double getCenterY() {
        return detectedBalls.isEmpty() ? -1 : detectedBalls.get(0).getCenterY();
    }

    public double getArea() {
        return detectedBalls.isEmpty() ? 0 : detectedBalls.get(0).getArea();
    }

    public String getColor() {
        return detectedBalls.isEmpty() ? "UNKNOWN" : detectedBalls.get(0).getColor();
    }
}