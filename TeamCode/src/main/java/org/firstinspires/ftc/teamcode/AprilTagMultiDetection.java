package org.firstinspires.ftc.teamcode;


import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;

    @Autonomous(name = "AprilTagMultiDetection", group = "Linear OpMode")
    public class AprilTagMultiDetection extends BaseOpMode {

        // Processor và Portal cho AprilTag
        private AprilTagProcessor aprilTagProcessor;
        private VisionPortal visionPortal;

        // Danh sách ID tag cần detect (5 tag: 20, 21, 22, 23, 24)
        private static final int[] TARGET_TAG_IDS = {20, 21, 22, 23, 24};

        @Override
        public void runOpMode() throws InterruptedException {
            // Khởi tạo robot
            initRobot();

            // Khởi tạo camera Logitech C720
            aprilTagProcessor = new AprilTagProcessor.Builder()
                    .setDrawTagOutline(true)  // Vẽ outline để debug
                    .build();

            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "Webcam 1"))  // Logitech C720
                    .setCameraResolution(new Size(640, 480))
                    .addProcessor(aprilTagProcessor)
                    .build();

            // Chờ start
            waitForStartWithTelemetry();

            // Vòng lặp chính: Nhận diện và đưa ra ID của từng tag
            while (opModeIsActive()) {
                // Lấy danh sách detections
                java.util.List<AprilTagDetection> detections = aprilTagProcessor.getDetections();

                // Xử lý detections và đưa ra ID
                if (!detections.isEmpty()) {
                    for (AprilTagDetection detection : detections) {
                        int tagID = detection.id;

                        // Kiểm tra nếu ID trong danh sách target (20, 21, 22, 23, 24)
                        if (isTargetTag(tagID)) {
                            String tagType = getTagType(tagID);  // Loại tag (Blue Goal, Red Goal, etc.)

                            // Telemetry: Đưa ra ID, loại, và thông tin cơ bản
                            telemetry.addData("AprilTag ID Detected", tagID);
                            telemetry.addData("Tag Type", tagType);
                            telemetry.addData("Center X (pixels)", detection.center.x);
                            telemetry.addData("Center Y (pixels)", detection.center.y);
                            telemetry.addData("Range (inches)", detection.ftcPose.range);
                            telemetry.addData("Bearing (degrees)", detection.ftcPose.bearing);
                        }
                    }
                } else {
                    telemetry.addData("AprilTag Detected", "None");
                    telemetry.addData("Status", "Scanning for AprilTags...");
                }

                telemetry.update();
                sleep(20);  // Tránh vòng lặp quá nhanh
            }

            // Dừng vision
            visionPortal.close();
            telemetry.addData("Status", "AprilTag Detection Stopped");
            telemetry.update();

            // stopRobot() tự động gọi
        }

        // Hàm tiện ích: Kiểm tra nếu ID là target (trong mảng TARGET_TAG_IDS)
        private boolean isTargetTag(int id) {
            for (int targetId : TARGET_TAG_IDS) {
                if (id == targetId) return true;
            }
            return false;
        }

        // Hàm tiện ích: Trả về loại tag dựa trên ID
        private String getTagType(int id) {
            switch (id) {
                case 20: return "Blue Goal";      // Goal xanh
                case 24: return "Red Goal";       // Goal đỏ
                case 21: return "Spike Mark 1";
                case 22: return "Spike Mark 2";
                case 23: return "Spike Mark 3";
                default: return "Unknown";
            }
        }
    }

