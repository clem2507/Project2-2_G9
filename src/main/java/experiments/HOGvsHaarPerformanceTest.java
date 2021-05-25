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
        haarDetector = new HaarCascadeFaceDetector(0);
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
    public long[] runExperiment() {
        long[] times = new long[2];
        times[0] = runHOGExperiment();
        times[1] = runHaarExperiment();
        return times;
    }

    // Runs the experiment with the HOG classifier
    public long runHOGExperiment() {
        long startTime = System.currentTimeMillis();
        hogDetector.findAABBs(image);
        long endTime = System.currentTimeMillis();
        return endTime-startTime;
    }

    // Runs the experiment with the Haar classifier
    public long runHaarExperiment() {
        long startTime = System.currentTimeMillis();
        haarDetector.findAABBs(image);
        long endTime = System.currentTimeMillis();
        return endTime-startTime;
    }



    public static void main(String[] args) {
        HOGvsHaarPerformanceTest experiment = new HOGvsHaarPerformanceTest("path");
    }
}
