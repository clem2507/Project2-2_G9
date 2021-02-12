package domains.Photo;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;

public class Screenshot extends JFrame {

    /**
     * Returns a screnshot taken from the webcam at the moment the class is called
     * @return
     */
    public BufferedImage takeSelfie()
    {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        VideoCapture videoCapture = new VideoCapture();
        Mat mat = new Mat();
        videoCapture.open(0);
        if(videoCapture.isOpened())
        {
            videoCapture.read(mat);
            Mat2Image mat2Image = new Mat2Image();
            videoCapture.release();
            return mat2Image.getImage(mat);
        }
        else return null;
    }

    public Screenshot() {
        /*System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        JPanel panel = new JPanel();
        JLabel label = new JLabel(new ImageIcon(takeSelfie()));
        panel.add(label);
        JFrame frame = this;
        frame.addFocusListener( new FocusListener() {
            private boolean gained = false;
            @Override
            public void focusGained( FocusEvent e ) {
                gained = true;
            }
            @Override
            public void focusLost( FocusEvent e ) {
                if ( gained ){
                    frame.dispose();
                }
            }
        } );
        this.add(panel);
        this.setSize(500,460);
        this.setUndecorated(true);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        this.setAlwaysOnTop(true);
        this.setVisible(true);*/
    }

}