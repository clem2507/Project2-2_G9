package backend.common.face_detection_api;

import backend.common.camera.Camera;
import image_processing.face_detection.Point2D;
import image_processing.face_detection.Rectangle;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a haar cascade face detector. It uses feature patches, or haar features, to scan
 * the image. These patches work as weak detectors, together they work a strong detectors.
 */
public class HaarCascadeFaceDetector implements FaceDetectorAPI{
    final int channel;
    final HaarCascadeDetector detector;

    public HaarCascadeFaceDetector(final int channel) {
        this.channel = channel;
        detector = new HaarCascadeDetector();
    }

    @Override
    public List<Rectangle> findAABBs(BufferedImage image) {
        final List<DetectedFace> faces = detector.detectFaces(ImageUtilities.createFImage(image));
        return faces.stream()
                .map(f -> {
                    final var bounds = f.getBounds();
                    final var topLeft = bounds.getTopLeft();
                    final var bottomRight = bounds.getBottomRight();
                    final Point2D pointA = new Point2D((int) topLeft.getX(), (int) topLeft.getY());
                    final Point2D pointB = new Point2D((int) bottomRight.getX(), (int) bottomRight.getY());
                    return new Rectangle(pointA, pointB);
                })
                .collect(Collectors.toList());
    }

}
