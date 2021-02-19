package backend.common.OS;

import nlp.Tokenizer;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CurrentOS {
    public static final double DEFAULT_MIN_THRESHOLD = 0.7;

    public static Set<File> find(final File root, final FilenameFilter filter, boolean recursive){
        Set<File> output = new HashSet<>(); // Create empty set of files found

        if(root.exists() && root.isDirectory()){ // If the root exists and is a directory
            File[] children = root.listFiles(); // Get files contained inside

            if(children != null) { // If the list of files inside of root is not null

                for (File child : children) { // For each file inside of root

                    if (recursive && child.isDirectory()) { // If search is recursive and file is a directory
                        output.addAll(find(child, filter, true)); // Search inside
                    } else if (child.isFile() && filter.accept(root, child.getName())) { // Else if is file and filter accepts
                        output.add(child); // Save file in files found
                    }

                }

            }

        }

        return output; // Return set of files found
    }

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
            // First we get the list of file that end with .app
            Set<File> appFiles = find(
                    (new File("/Applications")),
                    (dir, name) -> name.endsWith(".app"),
                    false);

            // Then we map them to ProgramReference
            return appFiles.stream()
                    .map(MacAppReference::new)
                    .collect(Collectors.toSet());
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
