package domains.Photo;

import backend.AssistantMessage;
import backend.Domain;
import backend.DomainNames;
import backend.Skill;
import nlp.MatchedSequence;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDate;
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
                    e.printStackTrace();
                }
                pushMessage("image-" + filename + ".png");
            }
        };
    }
}
