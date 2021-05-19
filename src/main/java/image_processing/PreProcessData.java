package image_processing;

import org.openimaj.image.FImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.openimaj.image.ImageUtilities.createFImage;

public class PreProcessData {

    private final BufferedImage image;
    private static final int WIDTH = SuperGlobalConstants.DETECTOR_WINDOW_WIDTH;
    private static final int HEIGHT = SuperGlobalConstants.DETECTOR_WINDOW_HEIGHT;

    public PreProcessData(BufferedImage image){
        this.image = resizeImage(image);
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Resizes the image to a 64x128 image and returns it.
     * @return new BufferedImage of size 64x128.
     */
    private static BufferedImage resizeImage(final BufferedImage image) {
        Image resizedImage = image.getScaledInstance(WIDTH,HEIGHT, Image.SCALE_SMOOTH);

        BufferedImage newImage = new BufferedImage(resizedImage.getWidth(null), resizedImage.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(resizedImage, 0, 0, null);
        graphics2D.dispose();

        //NOTE: This was slowing down everything. Avoid writing to disk in procedures that need to be fast.
        //ImageIO.write(newImage,"PNG", new File("resized.png"));
        return newImage;
    }


    /**
     * Finds pixels of the image and creates an array of these pixels
     * @return a matrix float[][] with all the pixels in the image.
     */
    private float[][] findPixels(){
        FImage images = createFImage(this.image);
        float[][] pixels = images.pixels;
        for (int i = 0; i < pixels.length ; i++){
            for(int j = 0; j < pixels[i].length ; j++){
                pixels[i][j] = pixels[i][j]*255;
            }
        }
        return pixels;
    }


    /**
     * Binning process where the histogram array is created and returned using the orientation and
     * @param gradientOrientation gradients orientations
     * @return 1d histogram array of type double[]
     */
    private double[] createHistogram(double[][] gradientOrientation, double[][] gradientMagnitude){

        double[] histogram = new double[9];
        int binSize = 20;
        for (int i = 0 ; i < gradientOrientation.length ; i++){
            for(int j = 0 ; j < gradientOrientation[i].length ; j++){
                int bin = (int) gradientOrientation[i][j]/binSize;
                //TODO: Double check this. We are not using it.
                //histogram[bin] += gradientMagnitude[i][j];
                histogram[bin]++;
            }
        }
        return histogram;
    }


    /**
     * Loops over all pixels of the image and then for every 8x8 patch it creates it's 9x1 feature matrix and adds it
     * to a 2d feature array.
     * @return Feature array(8x16) where each element is a feature vector for an 8x8 image.
     */
    private FeatureVector[][] get8x8Patches(){
        float[][] pixelsImage = findPixels();
        //create a 2d array which which store each
        FeatureVector [][] FeatureArray = new FeatureVector[HEIGHT/8][WIDTH/8];

        int countRowBig = 0;
        for (int i = 0 ; i < pixelsImage.length ; i=i+8){
            int countColBig = 0;
            for(int j = 0 ; j < pixelsImage[i].length ; j=j+8){

                float[][] pixels = new float[8][8];

                int countRow = 0;
                for (int x = i; x < i+8; x++){
                    int countCol = 0;
                    for (int y = j; y < j+8 ; y++){
                        pixels[countRow][countCol] = pixelsImage[x][y];
                        countCol++;
                    }
                    countRow++;
                }

                Gradients gradient = new Gradients(pixels);
                gradient.calcOrientation_Magnitude();
                double[] Hist = createHistogram(gradient.getGradientOrientation(), gradient.getGradientMagnitude());

                FeatureVector feature = new FeatureVector(Hist);
                feature.addAndGetList();

                FeatureArray[countRowBig][countColBig] = feature;
                countColBig++;
            }
            countRowBig++;
        }
        return FeatureArray;
    }

    /**
     * Concatenates 4 8x8 patches' feature vectors and normalizes them and then adds them the final feature list.
     * This list will then be fed to the SVM.
     * @return Feature list of size 3780.
     */
    public  List<Double>  getFeatureVector(){

        FeatureVector[][] smallFeatures = get8x8Patches();
        List<Double> featureVector = new ArrayList<>();

        for (int i = 0 ; i <smallFeatures.length && i+2 <=smallFeatures.length; i++){
            for(int j = 0 ; j < smallFeatures[0].length&& j+2 <=smallFeatures[0].length ; j++){
                ArrayList<Double> smallF = new ArrayList<>();
                double sum = 0;
                for(int x = i; x < i+2 && x<smallFeatures.length ; x++ ) {
                    for (int y = j; y < j + 2 && y<smallFeatures[0].length; y++) {
                        smallF.addAll(smallFeatures[x][y].getVectorList());
                        sum += smallFeatures[x][y].getSquaredSum();
                    }
                }

                double finalSum = sum;
                List<Double> result = smallF.stream().map(d -> d / finalSum).collect(Collectors.toList());
                featureVector.addAll(result);
            }
        }
        return featureVector;
    }
}
