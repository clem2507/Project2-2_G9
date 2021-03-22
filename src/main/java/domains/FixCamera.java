package domains;

import backend.Popup;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class FixCamera {

    private static int run = 0;

    public static boolean fix()
    {
        if(run > 1)
            return false;

        System.out.println("OpenCV fixed");
        run++;
        try {
            File source = new File("lib/opencv/opencv_java430.dll");
            Optional<String> path = Popup.userInput("Please paste the path to you jdk inside the bin folder.");
            File destination = new File(path.get() + "/opencv_java430.dll");
            FileUtils.copyFile(source,destination);
            return true;
        } catch (IOException e) {
            run = 0;
            e.printStackTrace();
            return false;
        }
    }
}
