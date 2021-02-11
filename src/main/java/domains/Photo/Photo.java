package domains.Photo;

import backend.*;
import nlp.MatchedSequence;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.BlockingQueue;

public class Photo extends Domain {
    public Photo(){
        super(DomainNames.Photo);

        addPattern("<photo>");
        addPattern("<selfie>");

    }

    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        System.out.println("SEQUENCE -->\t" + sequence);
        return new Skill(this, outputChannel) {
            @Override
            public void run() {

                FormatStyle dateStyle;
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");
                LocalDateTime now = LocalDateTime.now();
                String filename = dateTimeFormatter.format(now);
                Screenshot screenshot = new Screenshot();
                BufferedImage image = screenshot.takeSelfie();
                File outputImage = new File("src/assets/PhotoTaken/" + filename + ".png");
                try {
                    ImageIO.write(image, "jpg", outputImage);
                } catch (IOException e) {
                    try {
                        Files.createDirectories(Path.of("src/assets/PhotoTaken"));
                        pushMessage("Directory does not exist try again now.", MessageType.STRING);
                        return;
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
                pushMessage("image-" + filename + ".png", MessageType.IMAGE);
            }
        };
    }
}
