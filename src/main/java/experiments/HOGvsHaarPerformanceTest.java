package experiments;

import backend.common.face_detection_api.HOGFaceDetector;
import backend.common.face_detection_api.HaarCascadeFaceDetector;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class HOGvsHaarPerformanceTest {


    private BufferedImage image;
    private HOGFaceDetector hogDetector;
    private HaarCascadeFaceDetector haarDetector;

    public HOGvsHaarPerformanceTest(String path) {
        setImage(path);
        try {
            hogDetector = new HOGFaceDetector(0);
        }
        catch (IOException e) {

        }
        haarDetector = new HaarCascadeFaceDetector(1);
    }

    public void setImage(String path) {
        File imgFile = new File(path);
        try {
            this.image = ImageIO.read(imgFile);
        }
        catch (IOException e){

        }
    }

    // Runs experiment for both classifiers
    public double[] runExperiment() {
        double[] times = new double[2];
        times[0] = runHOGExperiment();
        times[1] = runHaarExperiment();
        return times;
    }

    // Runs the experiment with the HOG classifier
    public double runHOGExperiment() {
        //TODO time this
        hogDetector.findAABBs(image);
        return 0.0;
    }

    // Runs the experiment with the Haar classifier
    public double runHaarExperiment() {
        //TODO time this
        haarDetector.findAABBs(image);
        return 0.0;
    }



    public static void main(String[] args) {
        HOGvsHaarPerformanceTest experiment = new HOGvsHaarPerformanceTest("path");
    }
}
