package image_processing;

import javax.swing.*;
import java.awt.*;

public class QuickImageDisplay extends JPanel {
    protected Image image;

    public void setImage(final Image image) {
        this.image = image;
    }

    @Override
    public void paint(final Graphics g) {
        super.paint(g);
        g.drawImage(image, 0, 0, null);
    }

}
