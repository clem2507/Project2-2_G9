package image_processing;


import java.awt.image.BufferedImage;
import java.util.List;


public class HOG {
    private final List<Double> featureVector;

    public HOG(final BufferedImage image) {
        //get pixels then feed into preprocess data then retrieve the feature matrix from there
        PreProcessData preProcessData = new PreProcessData(image);
        this.featureVector = preProcessData.getFeatureVector();
    }

    public double[] getFeatureArray(){
        double[] featureArray = new double[this.featureVector.size()];
        int count = 0;
        for (double feature : this.featureVector){
            featureArray[count] = feature;
            count++;
        }

        return featureArray;
    }
}
