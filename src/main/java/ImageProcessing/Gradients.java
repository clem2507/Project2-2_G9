package ImageProcessing;

public class Gradients {

    private double[][] gradientMagnitude ; //array of each pixels grad magnitude
    private double[][] gradientOrientation; //array of each pixels grad orientation

    float[][] pixels; //pixels of the image

    public Gradients(float[][] pixels) {
        this.pixels = pixels;
    }


    public void calcOrientation_Magnitude(){
        gradientOrientation =  new double[pixels.length][pixels[0].length];
        gradientMagnitude = new double[pixels.length][pixels[0].length];

        float xG = 0;
        float yG = 0;
        for (int i = 0; i <pixels.length ; i++) {
            for (int j = 0; j < pixels[i].length; j++) {

                if (j == 0) {
                    xG = Math.abs(pixels[i][j + 1]);
                } else if (j == pixels[i].length - 1) {
                    xG = Math.abs(pixels[i][j - 1]);

                } else {
                    xG = Math.abs(pixels[i][j - 1] - pixels[i][j + 1]);
                }


                if (i == 0) {
                    yG = Math.abs(pixels[i + 1][j]);
                } else if (i == pixels.length - 1) {
                    yG = Math.abs(pixels[i - 1][j]);
                } else {
                    yG = Math.abs(pixels[i - 1][j] - pixels[i + 1][j]);
                }

                    gradientMagnitude[i][j] = Math.sqrt((xG)*(xG) + (yG)*(yG));

                    if(xG==0){
                        gradientOrientation[i][j] = Math.PI/2;
                    }else{
                        gradientOrientation[i][j] = Math.toDegrees(Math.atan((yG / xG)));
                    }
            }
        }
    }

    public double[][] getGradientOrientation() {
        return this.gradientOrientation;
    }

    public double[][] getGradientMagnitude() {
        return gradientMagnitude;
    }
}
