package backend.common.face_detection_api;

import image_processing.face_detection.Rectangle;

import java.awt.image.BufferedImage;
import java.util.List;

public class DetectionResults {
    private final BufferedImage image;
    private final List<Rectangle> AABBs;

    public DetectionResults(final BufferedImage image, final List<Rectangle> AABBs) {
        this.image = image;
        this.AABBs = AABBs;
    }

    public BufferedImage getImage() {
        return image;
    }

    public List<Rectangle> getAABBs() {
        return AABBs;
    }

}
