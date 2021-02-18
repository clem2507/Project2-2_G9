package domains;

import backend.*;
import backend.common.FileSystem;
import nlp.MatchedSequence;
import nlp.Tokenizer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class OpenApplication extends Domain {
    private final Set<FileSystem.LinkData> listedApps;
    private final String DEFAULT_LIST_PATH = "src/assets/ProjectData/linked_apps.txt";
    private final String SEPARATOR = ";", BLANK_SECTION = "<null>";

    public OpenApplication() {
        super(DomainNames.OpenApp);
        addPattern("<open, run, execute, exec, start> <@program_name, ...>");
        listedApps = new HashSet<>();
        loadListOfApps();
    }

    private void saveListOfApps(){
        List<String> lines = listedApps.stream()
                .map(l -> l.getLinkName() + SEPARATOR +
                        (l.getLinkDescription().isEmpty()? BLANK_SECTION:l.getLinkDescription()) + SEPARATOR +
                        l.getTarget() + SEPARATOR +
                        (l.getArgs().isEmpty()? BLANK_SECTION:l.getArgs()))
                .collect(Collectors.toList());
        Path pathToFile = Paths.get(DEFAULT_LIST_PATH);

        try {
            Files.write(pathToFile, lines, StandardCharsets.UTF_8);
        }

        catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadListOfApps(){
        File file = new File(DEFAULT_LIST_PATH);

        if(file.exists()){

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                listedApps.clear();
                String line;

                while ((line = reader.readLine()) != null){
                    List<String> data = Tokenizer.splitOn(line, ";")
                            .filter(t -> !t.equals(SEPARATOR)).collect(Collectors.toList());

                    listedApps.add(new FileSystem.LinkData(
                            data.get(0),
                            data.get(1).equals(BLANK_SECTION)? "":data.get(1),
                            data.get(2),
                            data.get(3).equals(BLANK_SECTION)? "":data.get(3)
                    ));
                }

            }

            catch (IOException e) {
                // This block of code should be impossible to reach. If we ever reach this line
                // that means we are doing something wrong somewhere else, maybe running this function twice.
                e.printStackTrace();
            }

        }

    }

    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        final Optional<Integer> programNameIndex = sequence.getSlotIndex("@program_name");
        final Optional<String> programName = programNameIndex.map(sequence::getStringAt);

        return new Skill(this, outputChannel) {

            private void searchApp(){ // This function represents the first step in the planned dialog
                // Here we try to search the app specified by the user among our list of "links" (i.e.
                // shortcuts)

                // We use Jaro-Winkler's score algorithm to measure the similarity between the name
                // specified by the user and the names we stored in our list of links.
                Optional<FileSystem.LinkData> bestMatch = FileSystem.findClosestMatch(
                        listedApps,
                        programName.get(),
                        FileSystem.DEFAULT_MIN_THRESHOLD
                );

                if(bestMatch.isPresent()){ // If we find a match
                    checkIfBroken(bestMatch.get()); // Step into the next node of our dialog graph
                    return; // Early skip to prevent the alternative path from running
                }

                rebuildList(); // If there is no match, that means we haven't listed the app. Then
                // we step into the alternative path
            }

            private void rebuildList(){ // This function represents the case/path in which the list of
                // apps must be updated

                // We ask the user with a popup if they want us to run a full system search to update this
                // list. It's important we ask first, because (at least on windows) it's a very resource intensive
                // task.
                if(Popup.binaryQuestion(
                        "\"" + programName.get() + "\"" + " is not in my library of linked apps." +
                                "\nDo you want me to run a full system search and link " + programName.get() + "?" +
                                "\nIt may take a while."
                )){
                    // First, let the user know that the search is taking place
                    // and also tell them that we can continue to serve them while they wait
                    pushMessage("Alright", MessageType.STRING);
                    pushMessage("I will notify you when the full system search finish", MessageType.STRING);
                    pushMessage("How may I help you until then?", MessageType.STRING);

                    try{
                        listedApps.clear(); // Clear old list
                        listedApps.addAll(FileSystem.listAllLinks()); // Save new list
                        saveListOfApps();

                        // Notify the user that the search is complete
                        pushMessage("Full system search finished", MessageType.STRING);
                        pushMessage("You can try running " + programName.get() + " again", MessageType.STRING);
                    } catch (FileSystem.UnsupportedOSException e) {
                        // In case of the operating system not being supported
                        e.printStackTrace();
                        pushMessage("Full system search interrupted", MessageType.STRING);
                        pushMessage(getUniqueName() + " does not support your operating system", MessageType.STRING);
                    }

                    return; // We early skip to prevent the alternative path from running
                }

                pushMessage("No full system search then", MessageType.STRING); // We say this to notify
                // the user we understand their choice of not running a full system search
            }

            private void checkIfBroken(FileSystem.LinkData link){ // Here we make sure the link is not broken
                // It may be the case that the link exists in our list, but the app is no longer in the same
                // path

                if(link.isBroken()){
                    pushMessage("It seems the link to " + programName.get() + " is broken", MessageType.STRING);
                    rebuildList();
                    return;
                }

                runProgram(link);
            }

            private void runProgram(FileSystem.LinkData link){ // Here we run the program
                int runTest = FileSystem.runProgramFromLink(link);

                if(runTest == 0){
                    pushMessage("Running " + programName.get(), MessageType.STRING);
                    return;
                }

                pushMessage("Something went wrong while trying to run " + programName.get(), MessageType.STRING);
            }

            @Override
            public void run() {

                if (programName.isPresent()) {

                    synchronized (listedApps){ // Since the skill is able to modify listedApps, we must lock it
                        // to prevent many threads from accessing at the same time.
                        searchApp(); // Run the first step of the dialog graph
                    }

                    return; // When we are done, early skip - essentially the same as if-else
                }

                pushMessage("No app name specified", MessageType.STRING);
            }

        };
    }

}
