package domains.Location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class CurrentLocation {
    public static String getLocation() throws IOException {

        // Get IP address
        URL whatismyip = new URL("http://checkip.amazonaws.com");
        BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

        String ip = in.readLine();

        // Get location based on IP
        String IP = ip;
        String API_KEY = "at_ldDTidowL2yO5ZA62ys6ixEWmG9Rh";
        String API_URL = "https://geo.ipify.org/api/v1?";
        String url = API_URL + "&apiKey=" + API_KEY + "&ipAddress=" + IP;

        String loc = "";

        try (java.util.Scanner s =
                     new java.util.Scanner(new java.net.URL(url).openStream())) {

            String result = s.useDelimiter("\\A").next();
            int ss = result.indexOf("city") + 7;
            for (int k = ss; k < result.length(); k++) {
                loc = loc + result.charAt(k);
                if (k == (result.indexOf("lat") - 4)) {
                    break;
                }
            }
            // System.out.println(s.useDelimiter("\\A").next());
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        // THIS PART IS USED SO IT PRINTS THE CORRECT OUTPUT WITHOUT JAVA UNICODE
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
            System.out.println(newstr);
            return newstr;
        } else {
            System.out.println(loc);
            return loc;
        }
    }
}
