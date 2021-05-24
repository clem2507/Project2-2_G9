package image_processing.SVM;
import libsvm.*;
import java.io.IOException;


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
//        UNCOMMENT WHEN WE WANT TO SAVE A MODEL
//        svm.svm_save_model("model.model", model);
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

}
