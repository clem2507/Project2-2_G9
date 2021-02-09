package domains.Weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CurrentWeather {

    public static String getWeather(String city) {
        String APIKEY = "54cd4b7942fb66b79b3377b338e3a9b1";
        String urlString = "http://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+APIKEY+"&units=metric";

        String temp = "";

        try{
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while((line = rd.readLine()) != null){
                result.append(line);
            }
            rd.close();

            int ss = result.indexOf("temp")+6;
            for (int k = ss ; k <result.length(); k++){
                if(isNumeric(String.valueOf(result.charAt(k)))|| result.charAt(k)=='.' || result.charAt(k)=='-'){
                    temp= temp+ result.charAt(k);
                }else{
                    break;
                }
            }
        }catch (IOException e){

        }
        return temp;
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
