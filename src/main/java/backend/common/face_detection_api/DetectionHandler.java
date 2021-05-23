package backend.common.face_detection_api;

import backend.common.camera.Camera;
import image_processing.SuperGlobalConstants;
import image_processing.face_detection.Rectangle;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static image_processing.face_detection.FaceDetector.preProcessCameraFeed;

public class DetectionHandler {
    private final int channel;
    private final FaceDetectorAPI[] detectors;

    private int selectedDetectorIndex;
    private volatile DetectionResults results;
    private volatile DetectorState state;


    public DetectionHandler(final int channel, final int defaultDetectorIndex) throws IOException {
        this.channel = channel;
        this.selectedDetectorIndex = defaultDetectorIndex;

        detectors = new FaceDetectorAPI[] {
                new HOGFaceDetector(channel),
                new HaarCascadeFaceDetector(channel)
        };
        assert defaultDetectorIndex < detectors.length;

        state = DetectorState.INERT;
        Camera.openCamera(channel);
    }

    public void detect() {
        assert state.equals(DetectorState.INERT);
        state = DetectorState.RUNNING;
        (new Thread(() -> {

            try {
                BufferedImage cameraFeed = Camera.getFrame(channel);
                assert cameraFeed != null;
                cameraFeed = preProcessCameraFeed(cameraFeed, SuperGlobalConstants.CAMERA_FEED_SIZE);

                List<Rectangle> detections = detectors[selectedDetectorIndex].findAABBs(cameraFeed);

                results = new DetectionResults(cameraFeed, detections);
            } catch (IOException e) {
                e.printStackTrace();
            }

            state = DetectorState.INERT;
        })).start();
    }

    public boolean isReady() {
        return state.equals(DetectorState.INERT);
    }

    public Optional<DetectionResults> getResults() {
        assert isReady();
        return Optional.ofNullable(results);
    }

    public void setDetector(final int index) {
        this.selectedDetectorIndex = index;
    }

}
