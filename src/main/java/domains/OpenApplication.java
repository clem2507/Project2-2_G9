package domains;

import backend.*;
import backend.common.OS.*;
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
    private final Set<ProgramReference> listedApps;
    private final String DEFAULT_LIST_PATH = "src/assets/ProjectData/linked_apps.txt";
    private boolean isOnMAC;

    public OpenApplication() {
        super(DomainNames.OpenApp);
        addPattern("<open, run, execute, exec, start> <@program_name, ...>");
        listedApps = new HashSet<>();
        isOnMAC = false; // False by default

        try {
            // Check if we are running on MACOS
            isOnMAC = CurrentOS.getOperatingSystem().equals(OSName.MAC);
        }

        catch (UnsupportedOSException e) {
            e.printStackTrace();
        }

        if(!isOnMAC){ // If this is not a MAC computer
            // Then we use our custom program reference system
            loadListOfApps();
        }

    }

    private void saveListOfApps(){
        List<String> lines = listedApps.stream()
                .map(ProgramReference::toString)
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

                    if(CurrentOS.getOperatingSystem().equals(OSName.WINDOWS)){
                        listedApps.add(new WindowsExeReference(line));
                    }

                    if(CurrentOS.getOperatingSystem().equals(OSName.MAC)){
                        listedApps.add(new MacAppReference(line));
                    }

                    // TODO: Add support for Linux

                }

            }

            catch (IOException | UnsupportedOSException e) {
                // This block of code should be impossible to reach. If we ever reach this line
                // that means we are doing something wrong somewhere else, maybe running this function twice.
                e.printStackTrace();
                file.delete(); // Get rid of the file - just in case it is corrupted
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
                Optional<ProgramReference> bestMatch = CurrentOS.findProgramReference(listedApps, programName.get());

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
                                "\nDo you want me to run a full system search and link \"" + programName.get() + "\"?" +
                                "\nIt may take a while."
                )){
                    // First, let the user know that the search is taking place
                    // and also tell them that we can continue to serve them while they wait
                    pushMessage("Alright", MessageType.STRING);
                    pushMessage("I will notify you when the full system search finish", MessageType.STRING);
                    pushMessage("How may I help you until then?", MessageType.STRING);

                    try{
                        listedApps.clear(); // Clear old list
                        listedApps.addAll(CurrentOS.getAllPrograms()); // Save new list
                        saveListOfApps();

                        // Notify the user that the search is complete
                        pushMessage("Full system search finished", MessageType.STRING);
                        pushMessage("You can try running " + programName.get() + " again", MessageType.STRING);
                    } catch (UnsupportedOSException e) {
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

            private void checkIfBroken(ProgramReference link){ // Here we make sure the link is not broken
                // It may be the case that the link exists in our list, but the app is no longer in the same
                // path

                if(link.isBroken()){
                    pushMessage("It seems the link to " + programName.get() + " is broken", MessageType.STRING);
                    rebuildList();
                    return;
                }

                runProgram(link);
            }

            private void runProgram(ProgramReference link){ // Here we run the program
                int runTest = link.start();

                if(runTest == 0){
                    pushMessage("Running " + programName.get(), MessageType.STRING);
                    return;
                }

                pushMessage("Something went wrong while trying to run " + programName.get(), MessageType.STRING);
            }

            @Override
            public void run() {

                if (programName.isPresent()) {

                    if(!isOnMAC) {

                        synchronized (listedApps) { // Since the skill is able to modify listedApps, we must lock it
                            // to prevent many threads from accessing at the same time.
                            searchApp(); // Run the first step of the dialog graph
                        }

                    } else {
                        // TODO: Procedure to run app in a MAC OS computer
                    }

                } else {
                    pushMessage("No app name specified", MessageType.STRING);
                }

            }

        };
    }

}
