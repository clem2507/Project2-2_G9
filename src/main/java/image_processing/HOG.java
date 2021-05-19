package image_processing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;


public class HOG {
    private BufferedImage image;
    private PreProcessData preProcessData;
    private List<Double> featureVector;

    public HOG(BufferedImage image) throws IOException {
        //get pixels then feed into preprocess data then retrieve the feature matrix from there
        this.image = image;
        this.preProcessData = new PreProcessData(this.image);
        this.image =  preProcessData.resizeImage();
        this.featureVector = preProcessData.getFeatureVector();
    }

    public  List<Double> getFeatureVector(){
        return this.featureVector;
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
