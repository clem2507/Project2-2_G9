package backend.common;

import nlp.Tokenizer;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class CurrentDesktop {
    public static final double DEFAULT_MIN_THRESHOLD = 0.5;

    public static class UnsupportedOSException extends Exception{

        public UnsupportedOSException(String error) {
            super(error);
        }
    }

    public static class LinkData{
        private final String linkName;
        private final String linkDescription;
        private final String execPath;
        private final String cmdArgs;

        public LinkData(String linkName, String linkDescription, String execPath, String cmdArgs){
            this.linkName = linkName;
            this.linkDescription = linkDescription;
            this.execPath = execPath;
            this.cmdArgs = cmdArgs;
        }

        public String getLinkName(){
            return linkName;
        }

        public String getLinkDescription(){
            return linkDescription != null? linkDescription:"";
        }

        public String getExecPath(){
            return execPath;
        }

        public String getArgs(){
            return cmdArgs != null? cmdArgs:"";
        }

        @Override
        public String toString() {
            return "[" + getLinkName() + ", " + getLinkDescription() + ", " + getExecPath() + ", " + getArgs() + "]";
        }
    }

    public static OSName getOperatingSystem() throws UnsupportedOSException {
        // detecting the operating system using os.name System property
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

    public static String getPathToDesktop(){
        return FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();
    }

    private static Set<LinkData> getLinksFromWindowsDesktop(){
        File desktopFolder = new File(getPathToDesktop()); // Get path to desktop
        assert desktopFolder.isDirectory(): "The specified path to Desktop is not a directory";
        Set<LinkData> output = new HashSet<>(); // Here we will annotate our data

        for(File file : Objects.requireNonNull(desktopFolder.listFiles())){ // For each file in the desktop

            if(!file.isDirectory() && file.getName().endsWith(".lnk")){ // If the file is not a directory and is a shortcut

                try {
                    WindowsShortcut shortcut = new WindowsShortcut(file); // Create shortcut object
                    String description = shortcut.getDescription(); // Get .lnk file description - could be null
                    String args = shortcut.getCommandLineArguments(); // Get .exe arguments - could be null

                    LinkData data = new LinkData(
                            file.getName(),
                            shortcut.getDescription(),
                            shortcut.getRealFilename(),
                            shortcut.getCommandLineArguments()
                    );
                    output.add(data);
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }

            }

        }

        return output;
    }

    private static Set<LinkData> getLinksFromDesktop() {

        try {

            if (getOperatingSystem().equals(OSName.WINDOWS)) {
                return getLinksFromWindowsDesktop();
            }

            if (getOperatingSystem().equals(OSName.LINUX)) {
                return Collections.emptySet(); // Not supported yet
            }

            if (getOperatingSystem().equals(OSName.MAC)) {
                return Collections.emptySet(); // Not supported yet
            }

        }

        catch (UnsupportedOSException e){
            e.printStackTrace();
        }

        return Collections.emptySet(); // This should never be reached
    }

    private static double computeLinkMatch(LinkData linkData, String programName){
        double tes1 = JaroWinklerScore.compute(linkData.getLinkName().toLowerCase(), programName.toLowerCase());
        double tes2 = JaroWinklerScore.compute(linkData.getLinkDescription().toLowerCase(), programName.toLowerCase());
        return Math.max(tes1, tes2);
    }

    public static Optional<LinkData> getMostSimilarLink(String programName, double minThreshold){
        // TODO: Looks dirty as hell, but I was too tired to write it in a different way - we will fix this later
        // - Dennis
        return getLinksFromDesktop().stream()
                .max(Comparator.comparingDouble(a -> computeLinkMatch(a, programName)))
                .stream()
                .filter(l -> computeLinkMatch(l, programName) >= minThreshold)
                .findAny();
    }

    public static void runProgramFromLink(LinkData link){

        try{

            if(getOperatingSystem().equals(OSName.WINDOWS)){
                List<String> args = Arrays.asList(link.getArgs().split("\\s+"));
                args = args.stream().map(String::trim).collect(Collectors.toList());

                List<String> params = new ArrayList<>(Collections.singletonList(link.getExecPath()));
                params.addAll(args);

                Process process = new ProcessBuilder(params).start();
            }

            // Other OS are not supported yet
        }

        catch (UnsupportedOSException | IOException e){
            e.printStackTrace();
        }

    }

}
