package image_processing.face_detection;

import backend.common.camera.Camera;
import image_processing.QuickImageDisplay;
import image_processing.SVM.FaceClassifier;
import image_processing.SuperGlobalConstants;
import org.w3c.dom.css.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FaceDetector {

    public static List<Rectangle> findAllFaces(
            final List<Rectangle> windows,
            final BufferedImage image,
            final FaceClassifier classifier
    ) {
        assert image.getHeight() == image.getWidth();

        return windows.parallelStream().filter(w -> isFace(w, image, classifier)).collect(Collectors.toList());
    }

    public static boolean isFace(final Rectangle rect, final BufferedImage image, final FaceClassifier classifier) {
        return classifier.predict(rect.getImageRegion(image)) == 1.0;
    }

    public static BufferedImage preProcessCameraFeed(final BufferedImage image, final int size) {
        return resizeRegion(getCentralRegion(image), size);
    }

    public static Point2D scaleWindowSize(final Point2D original, final double scale) {
        final double x = original.getX();
        final double y = original.getY();

        return new Point2D(
                (int) (x*scale),
                (int) (y*scale)
        );
    }

    public static BufferedImage resizeRegion(final BufferedImage image, final int size) {
        assert image.getWidth() == image.getHeight();

        Image resizedImage = image.getScaledInstance(
                size,
                size,
                Image.SCALE_SMOOTH);

        BufferedImage newImage = new BufferedImage(
                resizedImage.getWidth(null),
                resizedImage.getHeight(null),
                BufferedImage.TYPE_3BYTE_BGR
        );

        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(resizedImage, 0, 0, null);
        graphics2D.dispose();

        return newImage;
    }

    public static BufferedImage getCentralRegion(final BufferedImage image) {

        if(image.getHeight() <= image.getWidth()) {
            final double height = image.getHeight();
            final double width = image.getWidth();

            final Point2D topLeftCorner = new Point2D(
                    (int) ((width - height)/2.0),
                    0
            );
            final Point2D bottomRightCorner = new Point2D(
                    (int) ((width + height)/2.0),
                    (int) height
            );

            final Rectangle region = new Rectangle(topLeftCorner, bottomRightCorner);

            return region.getImageRegion(image);
        }

        throw new AssertionError(
                "Image height is larger than its width, this configuration is not supported yet."
        );
    }

    public static List<Rectangle> createWindows(
            final Point2D windowSize,
            final double relativeStepSize,
            final int imageWidth, final int imageHeight
    ) {
        final int MAX_X = imageWidth - windowSize.getX();
        final int MAX_Y = imageHeight - windowSize.getY();

        assert MAX_X >= 0;
        assert MAX_Y >= 0;

        final List<Rectangle> regions = new LinkedList<>();

        for(int x = 0; x < MAX_X; x += (int) (relativeStepSize*windowSize.getX())) {

            for(int y = 0; y < MAX_Y; y += (int) (relativeStepSize*windowSize.getY())) {
                final Rectangle window = new Rectangle(
                        new Point2D(x, y),
                        new Point2D(x + windowSize.getX(), y + windowSize.getY())
                );
                regions.add(window);
            }

        }

        return regions;
    }

}
