package backend.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Quote {
    public static String getQuote() throws IOException {
        final String urlString = "https://quotes.rest/qod?category=inspire";


        StringBuilder result = new StringBuilder();
        URL url = new URL(urlString);

        BufferedReader rd = null;
        try{
            URLConnection connection = url.openConnection();
            rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }catch(IOException e){

        }
        String quote = "";

        String line;
        if (rd != null){
            while((line = rd.readLine()) != null){
                result.append(line);
            }
            rd.close();
            int start = result.indexOf("quote")+49;
            for (int k = start ; k <result.length(); k++){
                if(result.charAt(k) != '"'){
                    quote= quote+ result.charAt(k);
                }else{
                    break;
                }
            }
        }



        if (quote.equals("")){
            quote = "Every saint has a past, and every sinner has a future.";
        }

        return quote;
    }

}
