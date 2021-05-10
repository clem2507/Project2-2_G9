package ImageProcessing.SVM;
import ImageProcessing.HOG;
import libsvm.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FaceClassifier {

    private svm_model model ;

    public FaceClassifier (){
    }

    /*
    To load a saved svm Model.
     */
    public void loadModel() throws IOException {
        this.model = svm.svm_load_model("model.model");
    }

    /*
    To train the svm model
    params: string arrays for the training set with faces and without faces
    return: void.
     */
    public void trainModel(String[] xTrainSet_FACE, String[] xTrainSet_NOTFACE) throws IOException {
        double[][] xtrain = new double[xTrainSet_FACE.length+xTrainSet_NOTFACE.length][3780];
        double[][] ytrain = new double[xTrainSet_FACE.length+xTrainSet_NOTFACE.length][1];

        int count = 0;
        for (String filename : xTrainSet_FACE){
            InputStream f = new FileInputStream(filename);
            BufferedImage image = ImageIO.read(f);
            HOG hog = new HOG(image);
            xtrain[count]= hog.getFeatureArray();
            ytrain[count]= new double[]{1.0};
            count++;
        }

        for (String filename : xTrainSet_NOTFACE){
            InputStream f = new FileInputStream(filename);
            BufferedImage image = ImageIO.read(f);
            HOG hog = new HOG(image);
            xtrain[count]= hog.getFeatureArray();
            ytrain[count]= new double[]{0.0};
            count++;
        }
        this.model = SVMFunctions.svmTrain(xtrain, ytrain);
    }

    /*
    To classify one image only.
    params: image to be classified
    return: double ie. either a 1 or 0 indicating face or not face respectively
     */
    public double predict(BufferedImage image) throws IOException {
        double[][] xtest = new double[1][3780];
        HOG hog = new HOG(image);
        xtest[0] = hog.getFeatureArray();
        return SVMFunctions.svmPredict(xtest, this.model)[0];
    }

    /*
    To classify multiple images at a time image only.
    params: An array of all the paths to the images.
    return: the predicted labels for each image.
    */
    public double[] predict(BufferedImage[] images) throws IOException {

        double[][] xtest = new double[images.length][3780];
        int count = 0;
        for (BufferedImage image: images){
            HOG hog = new HOG(image);
            xtest[count] = hog.getFeatureArray();
            count++;
        }

        return SVMFunctions.svmPredict(xtest, this.model);
    }


    public svm_model getModel() {
        return model;
    }


    public static void main(String[] args) throws IOException {
        //Instantiate the class
        FaceClassifier faceClassifier = new FaceClassifier();

        //Either load the model
        faceClassifier.loadModel();

        //Or train the svm and use the model
        String[] xFacesPaths = null;
        String[] xNotFacesPaths = null;
        faceClassifier.trainModel(xFacesPaths,xNotFacesPaths);

        String path = "/src/assets/bg3.jpg";
        InputStream f = new FileInputStream(path);
        BufferedImage exampleImage = ImageIO.read(f);
        double prediction = faceClassifier.predict(exampleImage);
        //Note that prediction is either a 1 or 0.
        // 1 for face and 0 for not face

        System.out.println(prediction);

    }
}
