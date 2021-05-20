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

    private static List<Rectangle> createWindows(
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

    public static void main(String[] args) {

        try {
            final JFrame displayFrame = new JFrame();
            final QuickImageDisplay imagePanel = new QuickImageDisplay();
            displayFrame.add(imagePanel);
            displayFrame.setVisible(true);
            displayFrame.setSize(800, 800);

            final FaceClassifier classifier = new FaceClassifier();
            classifier.loadModel();

            final List<Double> scalars = IntStream.rangeClosed(0, 4)
                    .asDoubleStream()
                    .map(i -> 1.0 + 1.15*i)
                    .boxed()
                    .collect(Collectors.toList());
            final Point2D windowSize = new Point2D(64, 128);

            final List<Rectangle> windows = scalars.stream()
                    .map(s -> scaleWindowSize(windowSize, s))
                    .flatMap(
                            w -> createWindows(
                                    w,
                                    0.5,
                                    SuperGlobalConstants.CAMERA_FEED_SIZE,
                                    SuperGlobalConstants.CAMERA_FEED_SIZE
                            ).stream()
                    ).collect(Collectors.toList());

            Camera.openCamera();

            System.out.println("Windows: " + windows.size());

            while (true) {
                BufferedImage cameraFeed = Camera.getFrame();
                assert cameraFeed != null;
                cameraFeed = preProcessCameraFeed(cameraFeed, SuperGlobalConstants.CAMERA_FEED_SIZE);

                final List<Rectangle> detectedFaces = findAllFaces(windows, cameraFeed, classifier);
                System.out.println("Faces: " + detectedFaces.size());

                final Optional<Rectangle> maxRect = detectedFaces.stream()
                        .max(Comparator.comparingInt(Rectangle::getArea));

                if(maxRect.isPresent()) {
                    imagePanel.setImage(maxRect.get().getImageRegion(cameraFeed));
                    displayFrame.getContentPane().repaint();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
            Camera.closeCamera();
        }

    }

}
