package backend;

import image_processing.face_detection.Rectangle;

import java.awt.image.BufferedImage;
import java.util.List;

public class FaceDetectionResult {
    public final BufferedImage image;
    public final List<Rectangle> aabbs;

    public FaceDetectionResult(final BufferedImage image, final List<Rectangle> aabbs) {
        this.aabbs = aabbs;
        this.image = image;
    }

}
