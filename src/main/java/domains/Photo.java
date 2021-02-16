package domains;

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
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Photo extends Domain {
    private final Map<List<Integer>, String> indexes2Pattern;
    private final Map<String, List<Integer>> pattern2Indexes;
    private static final String DIR = "src/assets/ProjectData/PhotoTaken"; // Default directory

    private static double fromScale(String spec) throws NLPError {

        if(Arrays.asList("seconds", "secs", "second").contains(spec)){
            return 1;
        }

        if(Arrays.asList("minutes", "mins", "mns", "minute").contains(spec)){
            return 60;
        }

        if(Arrays.asList("hours", "hrs", "hour").contains(spec)){
            return 120;
        }

        throw new NLPError("Illegal time scale " + spec);
    }

    public Photo(){
        super(DomainNames.Photo);

        // Since there are many ways to ask the assistant to take a photo, we
        // have to define many patterns.
        //
        // We also want the assistant to take a photo either immediately or wait an arbitrary amount of time
        // before taking the photo.
        // Our code should make our life easier, not harder. That's why we use the following technique.

        indexes2Pattern = new HashMap<>(); // Slot indexes to pattern
        pattern2Indexes = new HashMap<>(); // Pattern to slot indexes
        // In each list of indexes, we store an integer telling where to find the slot that holds
        // the data we want to look for. In this case, 0 --> time to wait and 1 --> time scale (i.e. secs, hrs, mins)

        indexes2Pattern.put(Arrays.asList(-1, -1), "<photo, selfie, picture>"); // Direct command - No waiting time

        // For cases such as 'take a picture in 5 seconds'
        indexes2Pattern.put(Arrays.asList(3, 4), "<photo, selfie, picture> <#:4> <in, after, wait> <param:int> <second, seconds, secs, minute, minutes, mins, mns, hour, hours, hrs>");

        // For those weirdos that would say 'wait 5 seconds, and then take a picture' XD
        indexes2Pattern.put(Arrays.asList(1, 2), "<in, after, wait> <param:int> <second, seconds, secs, minute, minutes, mins, mns, hour, hours, hrs> <#:5> <photo, selfie, picture>");

        // Then we have to add those patterns
        for (List<Integer> indexes : indexes2Pattern.keySet()){
            String pattern = indexes2Pattern.get(indexes);
            pattern2Indexes.put(pattern, indexes);
            addPattern(pattern);
        }
        // This way we kill two birds in one hit. We can add patterns with arbitrary locations for
        // the parameter, and wouldn't have to add anything else.

    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        String matchedPattern = sequence.getPattern(); // This is a copy of the pattern matched in the sequence
        int waitTime = 0; // Default to no wait time
        int scale = 1; // Default to 1-1 scale

        try {
            // Now we extract the waiting time and scale specified by the user, if any
            List<Integer> indexes = pattern2Indexes.get(matchedPattern);
            waitTime = indexes.get(0) != -1? sequence.getIntAt(indexes.get(0)):waitTime;
            scale = indexes.get(1) != -1? ((int) fromScale(sequence.getStringAt(indexes.get(1)))):scale;
        } catch (NLPError nlpError) {
            nlpError.printStackTrace();
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
        // Besides, this ensures this segment of the code runs only once per query. If more than 1 skill were trying
        // to create the same directory, at least one of them will crash or suffer an IOException.

        // We need to store some variables in finals - this is very technical to Java
        final int finalWaitTime = waitTime;
        final int finalScale = scale;

        return new Skill(this, outputChannel) {
            @Override
            public void run() {
                // The following code is to generate the name of the picture automatically, based on time
                // There is a possible scenario that will make two skills crash though, if they both happen
                // to run exactly at the same time, they will name the photo exactly the same. To fix this
                // we add the thread ID to the name, ensuring they are unique.
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();
                String filename = dateTimeFormatter.format(now) + "_" + Thread.currentThread().getId(); // + thread ID

                // We generate the relative file location
                String imagePath = DIR + "/" + filename + ".png";

                // Then we wait the specified time
                try {
                    int counter = 0;

                    while (counter < finalWaitTime*finalScale){
                        Thread.sleep(1000);
                        counter++;
                        pushMessage("Photo in " + counter + "!", MessageType.STRING);
                    }

                }

                catch (InterruptedException e) {
                    // If the thread is interrupted while waiting, we simply early stop
                    // don't even bother to take the picture
                    e.printStackTrace();
                    return; // Early stop
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