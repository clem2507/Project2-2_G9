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
            System.out.println("Image not processed");
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
        String path = "C:\\Users\\gebruiker\\Documents\\Project 2-2\\Code\\src\\assets\\ExperimentPictures\\1_face01.jpeg";

        HOGvsHaarPerformanceTest experiment = new HOGvsHaarPerformanceTest(path);
        long[] tempTimes = experiment.runExperiment();
        long averageTimeHogOne = tempTimes[0];
        long averageTimeHaarOne = tempTimes[0];
        for (int i = 0; i < 30; i++) {
            tempTimes = experiment.runExperiment();
            averageTimeHogOne = averageTimeHogOne + tempTimes[0];
            averageTimeHaarOne = averageTimeHaarOne + tempTimes[1];
        }
        averageTimeHogOne = averageTimeHogOne / 30;
        averageTimeHaarOne = averageTimeHaarOne / 30;
        System.out.println(averageTimeHogOne + " and " + averageTimeHaarOne);

        experiment.setImage("C:\\Users\\gebruiker\\Documents\\Project 2-2\\Code\\src\\assets\\ExperimentPictures\\10_face01.jpeg");

        tempTimes = experiment.runExperiment();
        long averageTimeHogTen = tempTimes[0];
        long averageTimeHaarTen = tempTimes[0];
        for (int i = 0; i < 30; i++) {
            tempTimes = experiment.runExperiment();
            averageTimeHogTen = averageTimeHogTen + tempTimes[0];
            averageTimeHaarTen = averageTimeHaarTen + tempTimes[1];
        }

        averageTimeHogTen = averageTimeHogTen / 30;
        averageTimeHaarTen = averageTimeHaarTen / 30;

        System.out.println(averageTimeHogTen + " and " + averageTimeHaarTen);

        experiment.setImage("C:\\Users\\gebruiker\\Documents\\Project 2-2\\Code\\src\\assets\\ExperimentPictures\\46_face01.jpeg");
        tempTimes = experiment.runExperiment();

        long averageTimeHogFifty = tempTimes[0];
        long averageTimeHaarFifty = tempTimes[0];
        for (int i = 0; i < 30; i++) {
            tempTimes = experiment.runExperiment();
            averageTimeHogFifty = averageTimeHogFifty + tempTimes[0];
            averageTimeHaarFifty = averageTimeHaarFifty + tempTimes[1];
        }

        averageTimeHogFifty = averageTimeHogFifty / 30;
        averageTimeHaarFifty = averageTimeHaarFifty / 30;
        System.out.println(averageTimeHogFifty + " and " + averageTimeHaarFifty);
    }
}
