package image_processing.face_detection;

import backend.common.camera.Camera;
import image_processing.SVM.FaceClassifier;
import image_processing.SuperGlobalConstants;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class FaceDetector {

    public static List<Rectangle> findAllFaces(
            final BufferedImage image,
            final Point2D scannerWindowSize,
            final List<Double> scalars,
            final FaceClassifier classifier) {
        assert image.getHeight() == image.getWidth();

        return scalars.parallelStream()
                .map(s -> scaleWindowSize(scannerWindowSize, s))
                .flatMap(r -> detectFaces(r, classifier, image).stream())
                .collect(Collectors.toList());
    }

    public static BufferedImage preProcessCameraFeed(final BufferedImage image, final int size) {
        return resizeRegion(getCentralRegion(image), size);
    }

    private static Point2D scaleWindowSize(final Point2D original, final double scale) {
        final double x = original.getX();
        final double y = original.getY();

        return new Point2D(
                (int) (x*scale),
                (int) (y*scale)
        );
    }

    private static BufferedImage resizeRegion(final BufferedImage image, final int size) {
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

    private static BufferedImage getCentralRegion(final BufferedImage image) {

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

    private static List<Rectangle> detectFaces(final Point2D windowSize, final FaceClassifier classifier, final BufferedImage image) {
        final int MAX_X = image.getWidth() - windowSize.getX();
        final int MAX_Y = image.getHeight() - windowSize.getY();
        final List<Rectangle> regions = new LinkedList<>();

        for(int x = 0; x < MAX_X; x++) {

            for(int y = 0; y < MAX_Y; y++) {
                final Rectangle window = new Rectangle(
                        new Point2D(x, y),
                        new Point2D(x + windowSize.getX(), y + windowSize.getY())
                );
                final BufferedImage section = window.getImageRegion(image);

                if(classifier.predict(section) >= 0.9) {
                    regions.add(window);
                }

            }

        }

        return regions;
    }

    public static void main(String[] args) {
        try {
            Camera.openCamera();

            while (true) {

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
