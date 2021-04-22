package ImageProcessing;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;


public class HOG {
    private BufferedImage image;
    private PreProcessData preProcessData;
    private List<Double> featureVector;

    public HOG(BufferedImage image) throws IOException {
        //get pixels then feed into preprocess data then retrieve the feature matrix from there
        this.preProcessData = new PreProcessData(image);
        this.image =  preProcessData.resizeImage();
        this.featureVector = preProcessData.getFeatureVector();
    }

    public  List<Double> getFeatureVector(){
        return this.featureVector;
    }
}
