package backend.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

// IMPORTANT: We don't use this anywhere. Do we need it? -Dennis
public class CurrentTime {
    public static String getTime(String continent, String city) {
        String urlString = "http://worldtimeapi.org/api/timezone/"+continent+"/"+city;

        String time = "";

        try {
            StringBuilder result = new StringBuilder();
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();


            int ss = result.indexOf("datetime")+22;
            for (int k = ss ; k <result.length(); k++){
                if(isNumeric(String.valueOf(result.charAt(k))) || result.charAt(k)==':'){
                    time= time+ result.charAt(k);
                }else{
                    break;
                }
            }

        } catch (IOException e) {

        }
        return time;
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
