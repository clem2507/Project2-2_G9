package backend.common.face_detection_api;

import image_processing.face_detection.Rectangle;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Represents an interface for continuous face detection APIs.
 */
public interface FaceDetectorAPI {

    /**
     * This method returns a list of rectangles (defined by two points) representing the location
     * of all faces found in an image.
     * @param image represents the image being analyzed.
     * @return a list of rectangles.
     */
    List<Rectangle> findAABBs(final BufferedImage image);

}
