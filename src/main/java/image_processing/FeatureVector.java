package image_processing;

import java.util.ArrayList;

public class FeatureVector {

    private double[] vector;
    private ArrayList<Double> vectorList = new ArrayList<>();
    private double squaredSum;


    public FeatureVector(double[] vector ) {
        this.vector = vector;
    }

    public double[] getVector() {
        return vector;
    }

    public void addAndGetList(){

        for (double x: this.vector){
            vectorList.add(x);
            squaredSum += x*x;
        }
    }

    public ArrayList<Double> getVectorList() {
        return vectorList;
    }

    public double getSquaredSum() {
        return squaredSum;
    }
}
