package ImageProcessing;

/*
Takes in an image then brings down the width to height ratio to 1:2
The image size should preferably be 64 x 128
This is because we will be dividing the image into 8*8 and 16*16 patches to extract the features. (For Simplicity)
Each 8x8 patch will return a 9x1 matrix of features (9 bins) so each 16x16 patch will return 36 features and
in the 64x128 image we have 105 16x16 patches so in total we will have 105*36 = features for every image.
 */

import org.openimaj.image.FImage;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import static org.openimaj.image.ImageUtilities.createFImage;

public class PreProcessData {

    private BufferedImage image;
    private final int WIDTH = 64;
    private final int HEIGHT = 128;
    private float[][] pixels;

    public PreProcessData(BufferedImage image){
        this.image = image;
    }

    /*
    Resize the image to a 64x128 image and returns it.
    @param:
    @return: preprocessed image
     */
    public BufferedImage resizeImage() throws IOException {
        Image resizeImage = this.image.getScaledInstance(WIDTH,HEIGHT,this.image.SCALE_SMOOTH);

        BufferedImage newImage = new BufferedImage(resizeImage.getWidth(null), resizeImage.getHeight(null), BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(resizeImage, 0, 0, null);
        graphics2D.dispose();

        this.image = newImage;
        ImageIO.write(newImage,"PNG", new File("resized.png"));
        return newImage;
    }


    /*
    Finds pixels of the image and creates an array of these pixels
    @param:
    @returns:
     */
    public float[][] findPixels(){
        FImage images = createFImage(this.image);
        this.pixels =  images.pixels;
        for (int i = 0 ; i < pixels.length ; i++){
            for(int j = 0 ; j < pixels[i].length ; j++){
                pixels[i][j] = pixels[i][j]*255;
            }
        }
        return pixels;
    }


    /*
    Binning process where the histogram array is created and returned using the orientation and
    @param: gradient orientation and magnitude matrices
    @returns: histogram 1d array
     */
    public double[] createHistogram(double[][] gradientOrientation, double[][] gradientMagnitude){

        double[] histogram = new double[9];
        int binSize = 20;
        for (int i = 0 ; i < gradientOrientation.length ; i++){
            for(int j = 0 ; j < gradientOrientation[i].length ; j++){
                int bin = (int) gradientOrientation[i][j]/binSize;
                histogram[bin] += gradientMagnitude[i][j];
            }
        }
        return histogram;
    }


    /*
    loops over all pixels of the image and then for every 8x8 patch it creates it's 9x1 feature matrix and adds it
    to a 2d feature array.
    @param:
    @returns: Feature array(8x16) where each element is a feature vector for an 8x8 image.
     */
    public FeatureVector[][] get8x8Patches(){
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


    /*
    concatenates 4 8x8 patches' feature vectors and normalizes them and then adds them the final feature list.
    This list will then be fed to the SVM.
    @param:
    @return: feature list which contains 3780 features of the image
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


//    public static void main (String[] args) throws IOException {
//        InputStream is = new FileInputStream("/Users/zein/IdeaProjects/Project2-2_G9/hello-world.png");
//        BufferedImage originalImage = ImageIO.read(is);
//        PreProcessData hi = new PreProcessData(originalImage);
//        System.out.println("processing image");
//        hi.resizeImage();
//        System.out.println("done processing");
//
//        System.out.println("getting features");
//        List<Double> featureVector = hi.getFeatureVector();
//        System.out.println(featureVector.size());
//
//    }


}