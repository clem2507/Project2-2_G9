package backend.camera;

import backend.camera.Mat2Image;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles the camera feed
 * Note: It is thread safe. Anyone can open a camera feed from anywhere and/or
 * tak a picture from any thread and the code will not break.
 *
 * Also, this class uses a similar technique to reference counting to keep track
 * of used camera - So, if you forget to close a camera, it will remain open until the program closes
 */
public class Camera {
    private static final Map<Integer, VideoCapture> videoCaptures = new HashMap<>(); // List of open cameras
    private static final Map<Integer, Integer> useCounter = new HashMap<>();

    /**
     * Open a new camera. If the camera is already open, it does nothing
     * @param channel the camera you want to open
     * @throws IOException if the camera cannot be opened
     */
    public static void openCamera(int channel) throws IOException {

        synchronized (videoCaptures){
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // This can be called multiple times
            // but it is not thread safe, so we put it inside of the synchronized block.

            if (!videoCaptures.containsKey(channel)){ // If the camera hasn't been open yet
                VideoCapture newCapture = new VideoCapture();
                newCapture.open(channel);

                if(!newCapture.isOpened()){
                    throw new IOException("Unable to open camera on channel " + channel);
                }

                useCounter.put(channel, 0); // Initialize counter to 0
                videoCaptures.put(channel, newCapture);
            }

            useCounter.put(channel, useCounter.get(channel) + 1); // Increment counter by 1
        }

    }

    /**
     * Closes a camera once it is no longer in use
     * @param channel
     */
    public static void closeCamera(int channel){

        synchronized (videoCaptures){

            if(videoCaptures.containsKey(channel)){
                VideoCapture videoCapture = videoCaptures.get(channel);
                useCounter.put(channel, useCounter.get(channel) - 1);

                if(useCounter.get(channel) <= 0){
                    useCounter.remove(channel);
                    videoCaptures.remove(channel);
                    videoCapture.release();
                }

            }

        }

    }

    /**
     * Returns a photo taken with the camera
     * @param channel camera to take the photo with
     * @return a BufferedImage representing the captured frame
     * @throws IOException if the video feed cannot be accessed
     */
    public static BufferedImage getFrame(int channel) throws IOException {
        Mat mat = new Mat();

        synchronized (videoCaptures){
            VideoCapture videoCapture = videoCaptures.get(channel);

            if(!videoCapture.isOpened()){
                throw new IOException("An error occurred while trying to read from camera");
            }

            videoCapture.read(mat);
            Mat2Image mat2Image = new Mat2Image();
            return mat2Image.getImage(mat);
        }

    }

    /**
     * Synonym of openCamera(0) - defaults to channel=0
     * @throws IOException
     */
    public static void openCamera() throws IOException {
        openCamera(0);
    }

    /**
     * Synonym of getFrame(0) - defaults to channel=0
     * @return
     * @throws IOException
     */
    public static BufferedImage getFrame() throws IOException {
        return getFrame(0);
    }

    /**
     * Synonym of closeCamera(0) - defaults to channel=0
     */
    public static void closeCamera(){
        closeCamera(0);
    }

}