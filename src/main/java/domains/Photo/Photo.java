package domains.Photo;

import backend.*;
import backend.camera.Camera;
import nlp.MatchedSequence;
import nlp.NLPError;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

public class Photo extends Domain {
    private final Map<Integer, List<String>> paramIndex;
    private static final String DIR = "src/assets/ProjectData/PhotoTaken"; // Default directory

    public Photo(){
        super(DomainNames.Photo);

        // Since there are many ways to ask the assistant to take a photo, we
        // have to define many patterns, since we also want the assistant
        // to take a photo either immediately or wait an arbitrary amount of time
        // before taking the photo, our code should make our life easier.

        paramIndex = new HashMap<>();
        // Define a map containing a parameter index (i.e. the index of the
        // slot that says how long to wait before taking a photo)

        paramIndex.put(-1, Arrays.asList("<photo, selfie, picture>")); // Direct command

        // For cases such as 'take a picture in 5 seconds'
        paramIndex.put(2, Arrays.asList("<photo, selfie, picture> <in, after> <param:int>"));

        // For those weirdos that would say 'wait 5 seconds, and then take a picture'
        paramIndex.put(0, Arrays.asList("<param:int> <#:4> <photo, selfie, picture>"));

        // Then we have to add those patterns
        for (Integer index : paramIndex.keySet()){

            for(String pattern : paramIndex.get(index)){
                addPattern(pattern);
            }

        }
        // This way we kill two birds in one hit. We can add patterns with arbitrary locations for
        // the parameter, and wouldn't have to add anything else.

    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        String matchedPattern = sequence.getPattern(); // This is a copy of the pattern matched in the sequence
        int inSlot = -1; // Default -1 (i.e. no parameter)

        for(Integer index : paramIndex.keySet()){ // Iterate over the indexes

            if(paramIndex.get(index).contains(matchedPattern)){ // If the pattern is related to the index
                inSlot = index; // Get index
                break; // Stop searching
            }

        }

        // Now, we have to make sure the default photos folder exists for the photo skills to actually
        // save the pictures we take.
        // NOTE: We add this code here and not in the skill itself because this is going to affect all
        // skills equally. (i.e. no reason to be skill specific)
        try {
            Files.createDirectories(Paths.get(DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int finalInSlot = inSlot;
        return new Skill(this, outputChannel) {
            @Override
            public void run() {
                // The following code is to generate the name of the picture automatically, based on time
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();
                String filename = dateTimeFormatter.format(now);

                // We generate the absolute file location
                String imagePath = DIR + "/" + filename + ".png";

                // Now we get the wait time from the matched pattern
                try {
                    int waitTime = finalInSlot >= 0? sequence.getIntAt(finalInSlot)*1000:0; // Time to wait in milliseconds
                    long startTime = System.currentTimeMillis();
                    long currentTime;
                    int counterState = 0;

                    // We use a while loop and not just Thread.wait or Thread.sleep
                    // in case the skill is interrupted while waiting.
                    do {
                        currentTime = System.currentTimeMillis();

                        // If the thread is interrupted  (i.e. the thread running this skill)
                        // then we early stop - don't even bother about taking the picture
                        if(Thread.interrupted()){
                            return;
                        }

                        int newCounterState = (int) ((currentTime - startTime)/1000);

                        if(newCounterState - counterState >= 1){
                            counterState = newCounterState;
                            pushMessage("Photo in " + counterState + "!", MessageType.STRING);
                        }

                    } while (currentTime - startTime < waitTime);

                } catch (NLPError nlpError) {
                    nlpError.printStackTrace();
                }

                // Here we take the picture
                try {
                    Camera.openCamera(0); // Open the camera feed
                    BufferedImage image = Camera.getFrame(0); // Get frame
                    Camera.closeCamera(0); // Close feed

                    // Save the image
                    File outputImage = new File(imagePath);
                    ImageIO.write(image, "png", outputImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Now we just have to tell the GUI to show this image.
                // We do this by sending the absolute path of the photo and specifying the
                // message type as IMAGE.
                pushMessage(imagePath, MessageType.IMAGE);
            }
        };
    }
}