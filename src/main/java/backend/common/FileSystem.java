package backend.common;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class FileSystem {
    public static final double DEFAULT_MIN_THRESHOLD = 0.5;

    public static class UnsupportedOSException extends Exception{

        public UnsupportedOSException(final String error) {
            super(error);
        }
    }

    public static class LinkData{
        private final String linkName;
        private final String linkDescription;
        private final String execPath;
        private final String cmdArgs;

        public LinkData(final String linkName, final String linkDescription, final String execPath, final String cmdArgs){
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

        public String getTarget(){
            return execPath;
        }

        public String getArgs(){
            return cmdArgs != null? cmdArgs:"";
        }

        public boolean isBroken(){
            return !(new File(getTarget())).exists();
        }

        @Override
        public String toString() {
            return getLinkName() + "{\nDescription: " + getLinkDescription() + "\nTarget: " + getTarget() + " " + getArgs() + "\n}";
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

    /**
     * Returns a set of absolute paths of files found in a directory (optionally, in the subdirectories as well)
     * @param directory File representing the path to the folder to search
     * @param filter FilenameFilter representing the discrimination criteria
     * @param recursive boolean - true if recursive search, false otherwise
     * @return Set<File> representing the absolute paths to the files found
     * @throws IOException if anything goes wrong
     */
    public static Set<File> listFiles(final File directory, final FilenameFilter filter, final boolean recursive) throws IOException {

        if(directory.exists() && directory.isDirectory()){ // If the path actually points to a folder
            Set<File> output = new HashSet<>(); // Set of files we have found so far
            File[] fileList = directory.listFiles(); // List of files inside of directory

            if(fileList != null) { // We check if the list is "accessible", because apparently Windows likes to be
                // inconsistent >:v

                for (File sub : fileList) { // For each item in the directory

                    if (sub.isDirectory() && recursive) { // If the item is a directory and this is a recursive search
                        output.addAll(listFiles(sub, filter, true)); // Search in the subfolder too
                    }

                    else if (filter.accept(directory, sub.getName())) { // Else if the file filter accepts the file
                        output.add(sub); // Store it
                    }

                }

            }

            return output; // Return the files of our search
            // NOTE: If anything goes wrong, like a subfolder not being accessible during a recursive search,
            // the resulting set will be an empty set. This is convenient.
        }

        throw new IOException("The specified path " + directory + " is not a directory");
    }

    public static Set<LinkData> listLinks(final File directory) throws UnsupportedOSException, IOException {

        if (getOperatingSystem().equals(OSName.WINDOWS)) {
            Set<File> links = listFiles(directory, (dir, name) -> name.endsWith(".lnk"), true);
            Set<LinkData> linkDataSet = new HashSet<>();

            for(File link : links){

                if(Thread.interrupted()){
                    throw new InterruptedIOException("Interrupted while listing files in " + directory.getAbsolutePath());
                }

                try {
                    WindowsShortcut shortcut = new WindowsShortcut(link);

                    if(shortcut.getRealFilename().endsWith(".exe")) {
                        LinkData data = new LinkData(
                                link.getName(),
                                shortcut.getDescription(),
                                shortcut.getRealFilename(),
                                shortcut.getCommandLineArguments()
                        );
                        linkDataSet.add(data);
                    }
                }

                catch (ParseException e) {
                    e.printStackTrace(); // Some shortcuts cannot be parsed by WindowsShortcut. Simply ignore those.
                }

            }

            return linkDataSet;
        }

        return Collections.emptySet();
    }

    public static Set<LinkData> listAllLinks() throws UnsupportedOSException {

        if(getOperatingSystem().equals(OSName.WINDOWS)){
            Set<LinkData> dataSet = new HashSet<>();

            for(File root : File.listRoots()){

                try {
                    dataSet.addAll(listLinks(root));
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

            }

            return dataSet;
        }

        return Collections.emptySet();
    }

    public static Optional<LinkData> findClosestMatch(Set<LinkData> dataSet, String programName, double minThreshold){
        return dataSet.stream()
                .max(Comparator.comparingDouble(a -> computeLinkMatch(a, programName)))
                .stream()
                .filter(l -> computeLinkMatch(l, programName) >= minThreshold)
                .findAny();
    }

    private static double computeLinkMatch(LinkData linkData, String programName){
        double tes1 = JaroWinklerScore.compute(linkData.getLinkName().toLowerCase(), programName.toLowerCase());
        double tes2 = JaroWinklerScore.compute(linkData.getLinkDescription().toLowerCase(), programName.toLowerCase());
        return Math.max(tes1, tes2);
    }

    public static int runProgramFromLink(LinkData link) {

        try {
            if(getOperatingSystem().equals(OSName.WINDOWS)){
                List<String> args = Arrays.asList(link.getArgs().split("\\s+"));
                args = args.stream().map(String::trim).collect(Collectors.toList());

                List<String> params = new ArrayList<>(Collections.singletonList(link.getTarget()));
                params.addAll(args);

                new ProcessBuilder(params).start();
                return 0; // Return 0 - meaning everything is OK
            }
        } catch (UnsupportedOSException | IOException e) {
            e.printStackTrace();
        }

        return -1; // Return anything other than zero - meaning something went wrong
    }

}
