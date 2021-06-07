package image_processing.SVM;
import image_processing.HOG;
import libsvm.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


public class SVMFunctions {


    /**
     * Trains an SVM model
     * @param: xtrain which are the feature vectors, ytrain which are the train labels
     * @return the trained svm
     */

    static svm_model svmTrain(double[][] xtrain, double[][] ytrain) throws IOException {
        int numOfImages = xtrain.length;
        int featureC = xtrain[0].length;

        svm_problem p = new svm_problem();
        p.y = new double[numOfImages];
        p.l = numOfImages;
        p.x = new svm_node[numOfImages][featureC];

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 1;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 20000;
        param.eps = 0.001;

        for (int i = 0; i < numOfImages; i++){
            double[] features = xtrain[i];
            p.x[i] = new svm_node[features.length];
            for (int j = 0; j < features.length; j++){
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                p.x[i][j] = node;
            }
            p.y[i] = ytrain[i][0];
        }

        svm_model model = svm.svm_train(p, param);
        /**UNCOMMENT WHEN YOU WANT TO SAVE THE NEW TRAINED MODEL*/
//        svm.svm_save_model("grayScale.model", model);
        return model;
    }


    /**
     * uses the trained model to predict labels of test images
     * @param: xtest which are the feature vectors of the test images, the svm model
     * @return ytest array of labels
     */
    static double[] svmPredict(double[][] xtest, svm_model model) {

        double[] yPred = new double[xtest.length];

        for(int k = 0; k < xtest.length; k++){

            double[] featureVector = xtest[k];
            svm_node[] nodes = new svm_node[featureVector.length];

            for (int i = 0; i < featureVector.length; i++) {
                svm_node n = new svm_node();
                n.index = i;
                n.value = featureVector[i];
                nodes[i] = n;
            }

            int[] labels = new int[2];
            svm.svm_get_labels(model,labels);
            double[] prob_estimates = new double[2];
            yPred[k] = svm.svm_predict_probability(model, nodes, prob_estimates);

            for(int j = 0; j < yPred.length; j++) {
                yPred[j] *= prob_estimates[j];
            }

        }

        return yPred;
    }

        public static void main(String [] args) throws IOException {
//            For loop through image file and for each, call HOG constructor then get its feature vector
//                   1 --> FACE | 0 --> NOT FACE
            String TrainFace = "src/assets/SVM Data/FaceTrain";
            List<File> trainFacefiles;
            trainFacefiles = Files.list(Paths.get(TrainFace))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            String TrainNoFace = "src/assets/SVM Data/NoFaceTrain";
            List<File> trainNoFacefiles;
            trainNoFacefiles = Files.list(Paths.get(TrainNoFace))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            String testFace = "src/assets/SVM Data/FaceTest";
            List<File> testFaceFiles;
            testFaceFiles = Files.list(Paths.get(testFace))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            String testNoFace = "src/assets/SVM Data/NoFaceTest";
            List<File> testNoFaceFiles;
            testNoFaceFiles = Files.list(Paths.get(testNoFace))
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .collect(Collectors.toList());
//
//            double[][] xtrain = new double[1273][3780];
//            double[][] ytrain = new double[1273][1];
            double[][] xtrain = new double[1318][3780];
            double[][] ytrain = new double[1318][1];

            //Training set with faces
            int count = 0;
            for (File file : trainFacefiles){
                if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))){
                    InputStream f = new FileInputStream(file);
                    BufferedImage image = ImageIO.read(f);
                    HOG hog = new HOG(image);
                    xtrain[count]= hog.getFeatureArray();
                    ytrain[count]= new double[]{1.0};
                    count++;
                }
            }
            System.out.println(count);
            //Training set without faces
            for (File file : trainNoFacefiles){
                if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg") )){

                    InputStream f = new FileInputStream(file);
                    BufferedImage image = ImageIO.read(f);
                    HOG hog = new HOG(image);
                    xtrain[count]= hog.getFeatureArray();
                    ytrain[count]= new double[]{0.0};
                    count++;
                }

            }

            System.out.println("Start Training");
            /** If you wish to train the model uncomment the line below, otherwise comment it and uncomment the line below it which
             * will load the model that is saved.*/
//            svm_model m = svmTrain(xtrain,ytrain);
            svm_model m = svm.svm_load_model("grayScale.model");
            System.out.println("Stopped Training");

            //Test set with no faces
            double[][] xtest = new double[548][3780];
            double[][] ytest = new double[548][1];
            int counter = 0;
            for (File file : testNoFaceFiles){
                if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))) {

                    InputStream f = new FileInputStream(file);
                    BufferedImage image = ImageIO.read(f);
                    HOG hog = new HOG(image);
                    xtest[counter] = hog.getFeatureArray();
                    ytest[counter] = new double[]{0.0};
                    counter++;

                }

            }

            //Test set with faces
            for (File file : testFaceFiles){
                if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))){
                    InputStream f = new FileInputStream(file);
                    BufferedImage image = ImageIO.read(f);
                    HOG hog = new HOG(image);
                    xtest[counter]= hog.getFeatureArray();
                    ytest[counter]= new double[]{1.0};
                    counter++;
                }
            }

            double[] ypred = svmPredict(xtest, m);
            for (int i = 0; i < xtest.length; i++){
                System.out.println("(Actual:" + ytest[i][0] + " Prediction:" + ypred[i] + ")");
            }
        }
}
