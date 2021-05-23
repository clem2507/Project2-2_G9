package image_processing.face_detection;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Rectangle {
    protected final Point2D pointA, pointB;

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

    public void draw(final BufferedImage image) {
        Graphics2D g = image.createGraphics();

        g.setStroke(new BasicStroke(2.0f));
        g.setColor(Color.red);
        g.drawRect(pointA.getX(), pointA.getY(), pointB.getX() - pointA.getX(), pointB.getY() - pointA.getY());
        g.dispose();
    }

    @Override
    public String toString() {
        return pointA.toString() + " -> " + pointB.toString();
    }

}
