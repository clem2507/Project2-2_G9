package image_processing.face_detection;

import backend.common.camera.Camera;
import image_processing.QuickImageDisplay;
import image_processing.SVM.FaceClassifier;
import image_processing.SuperGlobalConstants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FaceDetector {

    public static List<Rectangle> findAllFaces(
            final BufferedImage image,
            final Point2D scannerWindowSize,
            final List<Double> scalars,
            final FaceClassifier classifier,
            final double relativeStepSize
    ) {
        assert image.getHeight() == image.getWidth();

        return scalars.stream()
                .map(s -> scaleWindowSize(scannerWindowSize, s))
                .flatMap(r -> detectFaces(r, classifier, image, relativeStepSize).stream())
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

    private static List<Rectangle> detectFaces(
            final Point2D windowSize,
            final FaceClassifier classifier,
            final BufferedImage image,
            final double relativeStepSize
    ) {
        final int MAX_X = image.getWidth() - windowSize.getX();
        final int MAX_Y = image.getHeight() - windowSize.getY();
        final List<Rectangle> regions = new LinkedList<>();

        for(int x = 0; x < MAX_X; x += (int) (relativeStepSize*windowSize.getX())) {

            for(int y = 0; y < MAX_Y; y += (int) (relativeStepSize*windowSize.getY())) {
                final Rectangle window = new Rectangle(
                        new Point2D(x, y),
                        new Point2D(x + windowSize.getX(), y + windowSize.getY())
                );
                final BufferedImage section = window.getImageRegion(image);

                //System.out.println("Checking for a face in " + window);

                if(classifier.predict(section) >= 0.9) {
                    regions.add(window);
                }

            }

        }

        return regions;
    }

    public static void main(String[] args) {

        try {
            JFrame displayFrame = new JFrame();
            QuickImageDisplay imagePanel = new QuickImageDisplay();
            displayFrame.add(imagePanel);
            displayFrame.setVisible(true);
            displayFrame.setSize(SuperGlobalConstants.DETECTOR_WINDOW_WIDTH, SuperGlobalConstants.DETECTOR_WINDOW_HEIGHT);

            FaceClassifier classifier = new FaceClassifier();
            classifier.loadModel();

            List<Double> scalars = IntStream.rangeClosed(0, 4)
                    .asDoubleStream()
                    .map(i -> 1.0 + 1.15*i)
                    .boxed()
                    .collect(Collectors.toList());

            Point2D windowSize = new Point2D(64, 128);

            while (true) {
                Camera.openCamera();
                BufferedImage frame = Camera.getFrame();

                assert frame != null;
                frame = preProcessCameraFeed(frame, SuperGlobalConstants.CAMERA_FEED_SIZE);

                List<Rectangle> faces = findAllFaces(
                        frame,
                        windowSize,
                        scalars,
                        classifier,
                        0.5
                );

                System.out.println("Detected " + faces.size() + " faces.");
                Optional<Rectangle> maxFace = faces.stream().max(Comparator.comparingInt(Rectangle::getArea));

                if(maxFace.isPresent()) {
                    imagePanel.setImage(maxFace.get().getImageRegion(frame));
                    displayFrame.getContentPane().repaint();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Camera.closeCamera();
        System.out.println("Camera closed.");
    }

}
