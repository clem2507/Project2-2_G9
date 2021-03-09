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
        URLConnection connection = url.openConnection();
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        while((line = rd.readLine()) != null){
            result.append(line);
        }
        rd.close();

        int start = result.indexOf("quote")+49;
        String quote = "";
        for (int k = start ; k <result.length(); k++){
            if(result.charAt(k) != '"'){
                quote= quote+ result.charAt(k);
            }else{
                break;
            }
        }
        if (quote.equals("")){
            quote = "No Quote Available.";
        }

        return quote;
    }

}
