package backend.common.face_detection_api;

import backend.common.camera.Camera;
import image_processing.SVM.FaceClassifier;
import image_processing.SuperGlobalConstants;
import image_processing.face_detection.Point2D;
import image_processing.face_detection.Rectangle;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static image_processing.face_detection.FaceDetector.*;

/**
 * This class represents a histogram oriented gradients face detector. It extracts brightness gradients from
 * an image and uses a trained SVM (support vector machine) to determine if they belong to a human face or not.
 */
public class HOGFaceDetector implements FaceDetectorAPI {
    final FaceClassifier classifier;
    final List<Rectangle> windows;
    final int channel;

    public HOGFaceDetector(final int channel) throws IOException {
        classifier = new FaceClassifier();
        classifier.loadModel();

        final List<Double> scalars = IntStream.rangeClosed(0, 4)
                .asDoubleStream()
                .map(i -> 1.0 + 1.15*i)
                .boxed()
                .collect(Collectors.toList());
        final Point2D windowSize = new Point2D(64, 128);
        windows = scalars.stream()
                .map(s -> scaleWindowSize(windowSize, s))
                .flatMap(
                        w -> createWindows(
                                w,
                                0.5,
                                SuperGlobalConstants.CAMERA_FEED_SIZE,
                                SuperGlobalConstants.CAMERA_FEED_SIZE
                        ).stream()
                ).collect(Collectors.toList());

        this.channel = channel;
    }

    public HOGFaceDetector() throws IOException {
        this(0);
    }

    @Override
    public List<Rectangle> findAABBs(BufferedImage image) {
        final List<Rectangle> detectedFaces = findAllFaces(windows, image, classifier);

        //TODO: Merge overlapping rectangles.

        return detectedFaces;
    }

}
