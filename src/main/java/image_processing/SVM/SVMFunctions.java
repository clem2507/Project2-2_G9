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



    /*
    Method that trains the model
     */

    static svm_model svmTrain(double[][] xtrain, double[][] ytrain) throws IOException {
        svm_problem prob = new svm_problem();
        int recordCount = xtrain.length;
        int featureCount = xtrain[0].length;
        prob.y = new double[recordCount];
        prob.l = recordCount;
        prob.x = new svm_node[recordCount][featureCount];

        for (int i = 0; i < recordCount; i++){
            double[] features = xtrain[i];
            prob.x[i] = new svm_node[features.length];
            for (int j = 0; j < features.length; j++){
                svm_node node = new svm_node();
                node.index = j;
                node.value = features[j];
                prob.x[i][j] = node;
            }
            prob.y[i] = ytrain[i][0];
        }

        svm_parameter param = new svm_parameter();
        param.probability = 1;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.C = 1;
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.LINEAR;
        param.cache_size = 20000;
        param.eps = 0.001;

        svm_model model = svm.svm_train(prob, param);

        System.out.println("HELLO");
//        svm.svm_save_model("model.model", model);

        return model;
    }

    static double[] svmPredict(double[][] xtest, svm_model model)
    {

        double[] yPred = new double[xtest.length];

        for(int k = 0; k < xtest.length; k++){

            double[] fVector = xtest[k];

            svm_node[] nodes = new svm_node[fVector.length];
            for (int i = 0; i < fVector.length; i++)
            {
                svm_node node = new svm_node();
                node.index = i;
                node.value = fVector[i];
                nodes[i] = node;
            }

            int totalClasses = 2;
            int[] labels = new int[totalClasses];
            svm.svm_get_labels(model,labels);

            double[] prob_estimates = new double[totalClasses];
            yPred[k] = svm.svm_predict_probability(model, nodes, prob_estimates);

            for (int i = 0; i < totalClasses; i++){
                System.out.print("(" + labels[i] + ":" + prob_estimates[i] + ") ");
            }
            System.out.println();

            //TODO: SET A THRESHOLD
            double threshold = 0;
//            if(yPred[k] > threshold){
//                yPred[k] = 1;
//            }

            System.out.println("Final Prediction = " + yPred[k]);
        }
        return yPred;
    }







//    public static void main(String [] args) throws IOException {
//        //For loop through image file and for each, call HOG constructor then get its feature vector
//        //       1 --> FACE | 0 --> NOT FACE
////        String TrainFace = "src/assets/SVM Data/FaceTrain";
////        List<File> trainFacefiles;
////        trainFacefiles = Files.list(Paths.get(TrainFace))
////                .filter(Files::isRegularFile)
////                .map(Path::toFile)
////                .collect(Collectors.toList());
////
////        String TrainNoFace = "src/assets/SVM Data/NoFaceTrain";
////        List<File> trainNoFacefiles;
////        trainNoFacefiles = Files.list(Paths.get(TrainNoFace))
////                .filter(Files::isRegularFile)
////                .map(Path::toFile)
////                .collect(Collectors.toList());
//
//        String testFace = "src/assets/SVM Data/FaceTest";
//        List<File> testFaceFiles;
//        testFaceFiles = Files.list(Paths.get(testFace))
//                .filter(Files::isRegularFile)
//                .map(Path::toFile)
//                .collect(Collectors.toList());
//
//        String testNoFace = "src/assets/SVM Data/NoFaceTest";
//        List<File> testNoFaceFiles;
//        testNoFaceFiles = Files.list(Paths.get(testNoFace))
//                .filter(Files::isRegularFile)
//                .map(Path::toFile)
//                .collect(Collectors.toList());
//
////        double[][] xtrain = new double[1273][3780];
////        double[][] ytrain = new double[1273][1];
////
////        //Training set with faces
////        int count = 0;
////        for (File file : trainFacefiles){
////            if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))){
////                InputStream f = new FileInputStream(file);
////                BufferedImage image = ImageIO.read(f);
////                HOG hog = new HOG(image);
////                xtrain[count]= hog.getFeatureArray();
////                ytrain[count]= new double[]{1.0};
////                count++;
////
////            }
////
////        }
////        //Training set without faces
////        for (File file : trainNoFacefiles){
////            if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg") )){
////
////                InputStream f = new FileInputStream(file);
////                BufferedImage image = ImageIO.read(f);
////                HOG hog = new HOG(image);
////                xtrain[count]= hog.getFeatureArray();
////                ytrain[count]= new double[]{0.0};
////                count++;
////            }
////
////        }
//
//        System.out.println("Start Training");
////        svm_model m = svmTrain(xtrain,ytrain);
//        svm_model m = svm.svm_load_model("model.model");
//        System.out.println("Stopped Training");
//
//
//        //Test set with no faces
//        double[][] xtest = new double[547][3780];
//        double[][] ytest = new double[547][1];
//        int counter = 0;
//        for (File file : testNoFaceFiles){
//            if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))) {
//
//                InputStream f = new FileInputStream(file);
//                BufferedImage image = ImageIO.read(f);
//                HOG hog = new HOG(image);
//                xtest[counter] = hog.getFeatureArray();
//                ytest[counter] = new double[]{0.0};
//                counter++;
//
//            }
//
//        }
//
//        //Test set with faces
//        for (File file : testFaceFiles){
//            if(file.isFile() && (file.getName().toLowerCase().endsWith(".png") || file.getName().toLowerCase().endsWith(".jpg"))){
//                InputStream f = new FileInputStream(file);
//                BufferedImage image = ImageIO.read(f);
//                HOG hog = new HOG(image);
//                xtest[counter]= hog.getFeatureArray();
//                ytest[counter]= new double[]{1.0};
//                counter++;
//            }
//        }
//
//
//        long startTime = System.nanoTime();
//        double[] ypred = svmPredict(xtest, m);
//        long endTime = System.nanoTime();
//
//        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
//
//
//        int countz = 0;
//        for (int i = 0; i < xtest.length; i++){
//            System.out.println("(Actual:" + ytest[i][0] + " Prediction:" + ypred[i] + ")");
//            if(ytest[i][0]==ypred[i]){
//                countz++;
//            }
//        }
//
//        float acc = (countz/ypred.length);
//
//        System.out.println(counter);
//        System.out.println(ypred.length);
//        System.out.println(countz);
//        System.out.println(acc);
//        System.out.println(duration);
//
//    }

}
