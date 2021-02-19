package backend.common.OS;

import nlp.Tokenizer;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CurrentOS {
    public static final double DEFAULT_MIN_THRESHOLD = 0.7;

    public static Set<File> find(final File root, final FilenameFilter filter, boolean recursive){
        Set<File> output = new HashSet<>();

        if(root.exists() && root.isDirectory()){
            File[] children = root.listFiles();

            if(children != null) {

                for (File child : children) {

                    if (recursive && child.isDirectory()) {
                        output.addAll(find(child, filter, true));
                    } else if (child.isFile() && filter.accept(root, child.getName())) {
                        output.add(child);
                    }

                }

            }

        }

        return output;
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
            Set<File> appFiles = Arrays.stream(File.listRoots())
                    .flatMap(r -> find(r, (dir, name) -> name.endsWith(".app"), true).stream())
                    .collect(Collectors.toSet());

            // Then we map them to ProgramReference
            // Note we still check if the files exist and are files. Why? Because find(...) suffers the same
            // problem, file names won't always respect the standard string encoding formar and thus will be
            // impossible for us to read it properly.
            return appFiles.stream()
                    .filter(f -> f.exists() && f.isFile())
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
