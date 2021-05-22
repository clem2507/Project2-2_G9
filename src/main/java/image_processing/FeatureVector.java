package image_processing;

import java.util.LinkedList;
import java.util.List;

public class FeatureVector {

    private final double[] vector;
    private final List<Double> vectorList = new LinkedList<>();
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

    public List<Double> getVectorList() {
        return vectorList;
    }

    public double getSquaredSum() {
        return squaredSum;
    }
}
