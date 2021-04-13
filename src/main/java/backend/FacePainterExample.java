package backend;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.colour.RGBColour;
import org.openimaj.image.processing.face.detection.DetectedFace;
import org.openimaj.image.processing.face.detection.HaarCascadeDetector;
import org.openimaj.math.geometry.shape.Rectangle;

/**
 * Paint troll smile on all detected faces.
 *
 * @author Bartosz Firyn (SarXos)
 */
public class FacePainterExample extends JFrame implements Runnable, WebcamPanel.Painter {

    private static final long serialVersionUID = 1L;

    private static final Executor EXECUTOR = Executors.newSingleThreadExecutor();
    private static final HaarCascadeDetector detector = new HaarCascadeDetector();

    private Webcam webcam = null;
    private WebcamPanel.Painter painter = null;
    private List<DetectedFace> faces = null;
    private boolean faceExists = false;

    public FacePainterExample() throws IOException {

        super();

        webcam = Webcam.getDefault();
        webcam.setViewSize(WebcamResolution.VGA.getSize());
        webcam.open(true);

        WebcamPanel panel = new WebcamPanel(webcam, false);
        panel.setPreferredSize(WebcamResolution.VGA.getSize());
        panel.setPainter(this);
        panel.setFPSDisplayed(true);
        panel.setFPSLimited(true);
        panel.setFPSLimit(20);
        panel.setPainter(this);
        panel.start();

        painter = panel.getDefaultPainter();

        add(panel);

        setTitle("Face Detector Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        EXECUTOR.execute(this);

    }

    @Override
    public void run() {
        while (true) {
            if (!webcam.isOpen()) {
                return;
            }
            System.out.println("faces");
            faces = detector.detectFaces(ImageUtilities.createFImage(webcam.getImage()));
        }
    }

    @Override
    public void paintPanel(WebcamPanel panel, Graphics2D g2) {
        if (painter != null) {
            painter.paintPanel(panel, g2);
        }
    }

    @Override
    public void paintImage(WebcamPanel panel, BufferedImage image, Graphics2D g2) {

        if (painter != null) {
            painter.paintImage(panel, image, g2);
        }

        if (faces == null) {
            return;
        }

        Iterator<DetectedFace> dfi = faces.iterator();

        while (dfi.hasNext()) {
            this.faceExists = true;
            System.out.println("Face Detected");
            dfi.next();
        }

    }

    public boolean doesFaceExist(){
        return this.faceExists;
    }

    public static void main(String[] args) throws IOException {
        new FacePainterExample();

    }
}