package image_processing.face_detection;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rectangle {
    private static final int ROUND_RECTANGLE_CURVE_ANGLE = 45;
    protected final Point2D pointA, pointB;
    protected String label;

    public Rectangle(final Point2D topLeftCorner, final Point2D bottomRightCorner) {
        assert topLeftCorner.getX() <= bottomRightCorner.getX();
        assert topLeftCorner.getY() <= bottomRightCorner.getY();
        pointA = topLeftCorner;
        pointB = bottomRightCorner;
    }

    public BufferedImage getImageRegion(final BufferedImage image) {
        return image.getSubimage(
                pointA.getX(), pointA.getY(),
                pointB.getX() - pointA.getX(), pointB.getY() - pointA.getY()
        );
    }

    public int getArea() {
        return (pointB.getX() - pointA.getX()) * (pointB.getY() - pointA.getY());
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void draw(final BufferedImage image) {
        Graphics2D g = image.createGraphics();

        g.setStroke(new BasicStroke(3.0f));
        g.setColor(Color.red);
        g.drawRoundRect(
                pointA.getX(),
                pointA.getY(),
                pointB.getX() - pointA.getX(),
                pointB.getY() - pointA.getY(),
                ROUND_RECTANGLE_CURVE_ANGLE,
                ROUND_RECTANGLE_CURVE_ANGLE
        );

        if(label != null) {
            g.setColor(Color.green);
            g.setFont(g.getFont().deriveFont(35.0f));
            g.drawString(label, pointA.getX(), pointA.getY());
        }

        g.dispose();
    }

    @Override
    public String toString() {
        return pointA.toString() + " -> " + pointB.toString();
    }

}
