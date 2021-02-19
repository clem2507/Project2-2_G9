package backend.common.OS;

import nlp.Tokenizer;

import java.io.*;
import java.text.ParseException;
import java.util.*;

public class CurrentOS {
    public static final double DEFAULT_MIN_THRESHOLD = 0.7;

    public static OSName getOperatingSystem() throws UnsupportedOSException {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            return OSName.WINDOWS;
        }

        else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return OSName.LINUX;
        }

        else if (os.contains("mac")) {
            return OSName.MAC;
        }

        throw new UnsupportedOSException("The operating system is not supported");
    }

    public static String getCommandLineOutput(final String command){
        String result = null;

        try (
                InputStream inputStream = Runtime.getRuntime().exec(command).getInputStream();
                Scanner s = new Scanner(inputStream).useDelimiter("\\A")
        ) {
            result = s.hasNext() ? s.next() : null;
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return result != null? result:"";
    }

    public static Set<ProgramReference> getAllPrograms() throws UnsupportedOSException {

        if(getOperatingSystem().equals(OSName.WINDOWS)){ // If running on Windows
            String cmdOutput = getCommandLineOutput("where /r c:\\ *.lnk"); // Get list of all .lnk files
            String[] allPaths = cmdOutput.split("\\R+"); // Split the output in lines
            Set<ProgramReference> references = new HashSet<>();

            for(String path : allPaths){ // For each path to a .lnk file
                File file = new File(path);

                if(file.exists() && file.isFile()) { // If the file actually exists - due to string formatting
                    // sometimes the cmd tool will print file names that are not compatible with the standard
                    // conventions.

                    try {
                        WindowsShortcut shortcut = new WindowsShortcut(file); // Try parsing the .lnk file
                        references.add(new WindowsExeReference(file, shortcut)); // If all good, store a ref. to the program
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }

                }

            }

            return references;
        }

        if(getOperatingSystem().equals(OSName.MAC)){ // If running on MAC
            String cmdOutput = getCommandLineOutput("sudo find / -iname *.app"); // Get list of all .lnk files
            String[] allPaths = cmdOutput.split("\\R+"); // Split the output in lines
            Set<ProgramReference> references = new HashSet<>();

            for(String path : allPaths){ // For each path to a .app file
                System.out.println("Looking at " + path);
                File file = new File(path);

                if(file.exists() && file.isFile()) { // If the file actually exists - due to string formatting
                    // sometimes the cmd tool will print file names that are not compatible with the standard
                    // conventions.
                    System.out.println("\t- This path exists and is a file");
                    references.add(new MacAppReference(file)); // Store a ref. to the program
                }

            }

            return references;
        }

        // TODO: Add support for Linux

        return Collections.emptySet();
    }

    public static Optional<ProgramReference> findProgramReference(final Set<ProgramReference> references, final String name){
        return references.stream()
                .max(Comparator.comparingDouble(a -> a.computeSimilarity(name)))
                .stream().filter(r -> r.computeSimilarity(name) >= DEFAULT_MIN_THRESHOLD).findAny();
    }

}
