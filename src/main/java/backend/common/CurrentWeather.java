package backend.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class CurrentWeather {

    public static StringBuilder getWeather(String city) throws IOException {
        final String APIKEY = "54cd4b7942fb66b79b3377b338e3a9b1";
        final String urlString = "http://api.openweathermap.org/data/2.5/weather?q="+ city +"&appid="+APIKEY+"&units=metric";


        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while((line = rd.readLine()) != null){
            result.append(line);
        }
        rd.close();

        return result;
    }
}
