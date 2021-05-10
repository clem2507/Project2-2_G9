package backend.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;

public class CurrentLocation {
    public static String getLocation() throws IOException {

        String API_KEY = "at_ci2SFOqsrPElmtSt0BN4LLq2XY5cu";
        String API_URL = "https://ip-geolocation.whoisxmlapi.com/api/v1?";
        String url = API_URL + "apiKey=" + API_KEY;

        String loc = "";

        java.util.Scanner s = new java.util.Scanner(new java.net.URL(url).openStream());
        String result = s.useDelimiter("\\A").next();
        int ss = result.indexOf("city") + 7;
        for (int k = ss; k < result.length(); k++) {
            loc = loc + result.charAt(k);
            if (k == (result.indexOf("lat") - 4)) {
                break;
            }
        }


        // THIS PART IS USED SO IT PRINTS THE CORRECT OUTPUT WITHOUT JAVA UNICODE, i.e. \u00fc is actually "ü"
        String newstr = "";
        if (loc.contains("\\u00fc")) {
            int index = loc.indexOf("\\") - 1;
            String replc = loc.replace("\\u00fc", "");

            for (int i = 0; i < replc.length(); i++) {
                newstr += replc.charAt(i);
                if (i == index) {
                    newstr += "u";
                }
            }
            System.out.println(newstr);
            return newstr;
        }

        if (loc.contains("\\u00c4")) {
            int index = loc.indexOf("\\") - 1;
            String replc = loc.replace("\\u00c4", "");

            for (int i = 0; i < replc.length(); i++) {
                newstr += replc.charAt(i);
                if (i == index) {
                    newstr += "a";
                }
            }
            System.out.println(newstr);
            return newstr;
        }

        if (loc.contains("\\u00d6")) {
            int index = loc.indexOf("\\") - 1;
            String replc = loc.replace("\\u00c6", "");

            for (int i = 0; i < replc.length(); i++) {
                newstr += replc.charAt(i);
                if (i == index) {
                    newstr += "o";
                }
            }
            System.out.println(newstr);
            return newstr;
        }

        if (loc.contains("\\u00df")) {
            int index = loc.indexOf("\\") - 1;
            String replc = loc.replace("\\u00df", "");

            for (int i = 0; i < replc.length(); i++) {
                newstr += replc.charAt(i);
                if (i == index) {
                    newstr += "ss";
                }
            }
            return newstr;
        } else {
            return loc;
        }
    }
}
