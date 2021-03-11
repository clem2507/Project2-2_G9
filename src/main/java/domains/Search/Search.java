package domains.Search;

import backend.common.OS.CurrentOS;
import backend.common.OS.OSName;
import backend.common.OS.UnsupportedOSException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class Search {

    public static ArrayList search(String keyword) throws IOException, UnsupportedOSException {

        String url = "http://www.google.com/search"+"?q="+keyword;

        Document doc = Jsoup.connect(url).get();
        String html = doc.html();

        Files.write(Paths.get("file.txt"), html.getBytes());

        Elements links = doc.select("cite");

        List<String> linkss = new ArrayList<>();
        for(Element link : links){
            String current  = link.text();

            if(CurrentOS.getOperatingSystem() == OSName.WINDOWS) {
                for (int i = 0; i < current.length(); i++) {
                    if (Character.UnicodeBlock.of(current.charAt(i)) != Character.UnicodeBlock.BASIC_LATIN) {
                        current = current.replaceAll(" "+current.charAt(i)+" ", "/");
                    }
                }
            }else if(CurrentOS.getOperatingSystem() == OSName.WINDOWS) {
                if(current.contains(" › ")){
                    current = current.replaceAll(" › ", "/");
                }
            }
            if(current.contains("...")){
                current = current.replace("...", "");
            }
            linkss.add(current);
        }

        LinkedHashSet<String> hashSet = new LinkedHashSet<>(linkss);
        ArrayList<String> noduplicates = new ArrayList<>(hashSet);

        for(String k : noduplicates){
            System.out.println(k);
        }

        FileWriter writer = new FileWriter("output.txt");
        for(String str: noduplicates) {
            writer.write(str + System.lineSeparator());
        }
        writer.close();

        return noduplicates;
    }

    public static void open(String keyword) throws IOException, UnsupportedOSException {
        String os = System.getProperty("os.name").toLowerCase();

        if(CurrentOS.getOperatingSystem() == OSName.MAC){
            Runtime rt = Runtime.getRuntime();
            String url = "http://" + keyword;
            System.out.println("opening "+ "http://" + keyword);
            rt.exec("open " + url);
            System.out.println("opened "+ "http://" + keyword);
        }else if(CurrentOS.getOperatingSystem() == OSName.WINDOWS){
            Runtime rt = Runtime.getRuntime();
            String url = "http://" + keyword;
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        }
    }

    public static void googleSearch(String keyword) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        if(os.indexOf("mac") >= 0){
            Runtime rt = Runtime.getRuntime();
            String url = "http://www.google.com/search"+"?q="+keyword;
            rt.exec("open " + url);
        }else if(os.indexOf("win") >= 0){
            Runtime rt = Runtime.getRuntime();
            String url = "http://www.google.com/search"+"?q="+keyword;
            rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
        }
    }
}
