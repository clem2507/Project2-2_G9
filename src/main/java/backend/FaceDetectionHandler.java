package backend;

import backend.common.camera.Camera;
import backend.common.face_detection_api.FaceDetectorAPI;
import backend.common.face_detection_api.HOGFaceDetector;
import backend.common.face_detection_api.HaarCascadeFaceDetector;
import image_processing.SuperGlobalConstants;
import image_processing.face_detection.Rectangle;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static image_processing.face_detection.FaceDetector.preProcessCameraFeed;

//IMPORTANT: This approach assumes both face detectors work with a resolution
// of 720x720. It is not elegant, but we have no time to make a dynamic
// system that configures itself to the needs of each detector.

public class FaceDetectionHandler {
    private volatile int selectedDetector;
    private final int channel;
    private final FaceDetectorAPI[] detectorAPIs;
    private final BlockingQueue<FaceDetectionResult> queue;

    class DetectionProcess implements Runnable {

        @Override
        public void run() {
            try {
                BufferedImage cameraFeed = Camera.getFrame(channel);
                assert cameraFeed != null;
                cameraFeed = preProcessCameraFeed(cameraFeed, SuperGlobalConstants.CAMERA_FEED_SIZE);
                List<Rectangle> detections = detectorAPIs[selectedDetector].findAABBs(cameraFeed);
                queue.put(new FaceDetectionResult(cameraFeed, detections));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public FaceDetectionHandler(final int channel, final int selectedDetector) throws IOException {
        queue = new LinkedBlockingQueue<>();
        this.selectedDetector = selectedDetector;
        this.channel = channel;

        detectorAPIs = new FaceDetectorAPI[2];

        detectorAPIs[0] = new HOGFaceDetector(channel);
        detectorAPIs[1] = new HaarCascadeFaceDetector(channel);

        Camera.openCamera(channel);
        runDetection();
    }

    public void runDetection() {
        (new Thread(new DetectionProcess())).start();
    }

    public void setSelectedDetector(final int i) {
        System.out.println("Changing to detector Nr " + i);
        selectedDetector = i;
    }

    public Optional<FaceDetectionResult> getDetectionsAndContinue() {
        return Optional.ofNullable(queue.poll());
    }

    public boolean hasMoreResults() {
        return !queue.isEmpty();
    }

}
