package domains;

import backend.*;
import backend.common.camera.Camera;
import nlp.MatchedSequence;
import nlp.NLPError;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;

public class Photo extends Domain {
    private static final String DIR = "src/assets/ProjectData/PhotoTaken"; // Default directory
    private static String absPath = "C:\\Users\\aless\\IdeaProjects\\Project2-2_G9\\lib\\";

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

        // Pay attention to the new slot tagging system

        addPattern("<photo, selfie, picture>");
        addPattern("<photo, selfie, picture> <#:4> <in, after, wait> <@wait_time, param:int> <@time_scale, second, seconds, secs, minute, minutes, mins, mns, hour, hours, hrs>");
        addPattern("<in, after, wait> <@wait_time, param:int> <@time_scale, second, seconds, secs, minute, minutes, mins, mns, hour, hours, hrs> <#:5> <photo, selfie, picture>");

        // For the last two patterns, which support waiting and different time scales (i.e. secs, mins, hrs), we add
        // the tags @wait_time and @time_scale to the corresponding slots

    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        String matchedPattern = sequence.getPattern(); // This is a copy of the pattern matched in the sequence

        Optional<Integer> waitTimeSlotIndex = sequence.getSlotIndex("@wait_time"); // Here we try getting the
        // slot tagged as @wait_time - however, if it is not present, we get an empty Optional
        Optional<Integer> timeScaleSlotIndex = sequence.getSlotIndex("@time_scale"); // Same case as the one above

        int waitTime = 0, scale = 1; // Scale defaults to 1 - It shouldn't matter anyways

        try {
            waitTime = waitTimeSlotIndex.isPresent()? sequence.getIntAt(waitTimeSlotIndex.get()):0; // Default to no wait time

            if(timeScaleSlotIndex.isPresent()){
                scale = (int) fromScale(sequence.getStringAt(timeScaleSlotIndex.get()));
            }

        }

        catch (NLPError e){
            e.printStackTrace();
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
                    System.out.println(System.getenv("JAVA_HOME"));
                } catch (UnsatisfiedLinkError | Exception e) {

                    if(System.getProperty("os.name").startsWith("Windows"))
                    {

                        String javaPath = "C:\\Users\\aless\\.jdks\\openjdk-14.0.2\\bin";
                        System.out.println("Updating Lib + -" + javaPath);
                        File nativeLib = new File("lib/opencv/opencv_java430.dll");
                        File nativeLibToHome = new File(javaPath + "/opencv_java430.dll");
                        try {
                            FileUtils.copyFile(nativeLib.getAbsoluteFile(),nativeLibToHome);
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                            System.out.println("Failed Lib");
                        }
                    }
                    else
                    {
                        pushMessage("Please open the build framework file inside the libraries folder", MessageType.STRING);
                    }


                    pushMessage("Libraries error please try again.", MessageType.STRING);
                    return;
                }

                // Now we just have to tell the GUI to show this image.
                // We do this by sending the absolute path of the photo and specifying the
                // message type as IMAGE.
                pushMessage(imagePath, MessageType.IMAGE);
            }
        };
    }

}