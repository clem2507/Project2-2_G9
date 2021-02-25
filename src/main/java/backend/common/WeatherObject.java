package backend.common;

import java.io.IOException;

public class WeatherObject {

    private StringBuilder result;
    private String temp;
    private String maxTemp;
    private String minTemp;
    private String feelsLike;
    private String windSpeed;
    private String humidity;
    private String visibility;


    public WeatherObject(String city) throws IOException {
        this.result = CurrentWeather.getWeather(city);
        this.temp = retrieveTemp();
        this.maxTemp = retrieveMaxTemp();
        this.minTemp = retrieveMinTemp();
        this.feelsLike = retrieveFeelsLike();
        this.windSpeed = retrieveWindSpeed();
        this.humidity = retrieveHumidity();
        this.visibility = retrieveVisibility();
    }

    public String retrieveTemp(){
        int tempWord = this.result.indexOf("temp")+6;
        return retreiveInfo(tempWord);
    }

    public String retrieveMaxTemp(){
        int maxtempWord = result.indexOf("temp_max")+10;
        return retreiveInfo(maxtempWord)+"'C";
    }

    public String retrieveMinTemp(){
        int mintempWord = result.indexOf("temp_min")+10;
        return retreiveInfo(mintempWord)+"'C";
    }

    public String retrieveFeelsLike(){
        int feelsLikeWord = result.indexOf("feels_like")+12;
        return retreiveInfo(feelsLikeWord)+"'C";
    }

    public String retrieveWindSpeed(){
        int windWord = result.indexOf("wind")+15;
        return retreiveInfo(windWord)+" m/s";
    }


    public String retrieveHumidity(){
        int humidityWord = result.indexOf("humidity")+10;
        return retreiveInfo(humidityWord)+"%";
    }

    public String retrieveVisibility(){
        int visibiltyWord = result.indexOf("visibility")+12;
        return retreiveInfo(visibiltyWord)+" m";
    }

    public String getTemp() {
        return temp;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getFeelsLike() {
        return feelsLike;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getVisibility() {
        return visibility;
    }

    public String retreiveInfo(int start){
        String info = "";
        for (int k = start ; k <this.result.length(); k++){
            if(isNumeric(String.valueOf(this.result.charAt(k)))|| this.result.charAt(k)=='.' || this.result.charAt(k)=='-'){
                info= info+ this.result.charAt(k);
            }else{
                break;
            }
        }
        return info;
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
