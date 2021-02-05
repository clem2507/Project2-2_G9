package domains.Calendar;

public class Time {
    int minutes;
    int seconds;

    /**
     * Constructor to create a time class
     * @param minutes
     * @param seconds
     */
    public Time(int minutes, int seconds)
    {
        this.minutes = minutes;
        this.seconds = seconds;
    }

    /**
     * Return minutes
     * @return
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Return seconds
     * @return
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * Return a String of the time
     * @return
     */
    @Override
    public String toString() {
        return  minutes +
                ":" + seconds;
    }
}
