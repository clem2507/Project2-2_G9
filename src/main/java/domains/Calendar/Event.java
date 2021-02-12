package domains.Calendar;

public class Event {
    private Time startTime;
    private Time endTime;
    private Date date;
    private String summary;
    private String location;

    /**
     * Get's the following parameters and it creates an event
     * The date is got from the parameter startTimeRaw
     * @param startTimeRaw
     * @param endTimeRaw
     * @param summaryRaw
     * @param locationRaw
     */
    public Event(String startTimeRaw, String endTimeRaw, String summaryRaw, String locationRaw)
    {

        if(startTimeRaw.split(":")[1].split("T").length > 1)
            this.date = new Date(Integer.valueOf(startTimeRaw.split(":")[1].split("T")[0].substring(0,4)), Integer.valueOf(startTimeRaw.split(":")[1].split("T")[0].substring(4,6)), Integer.valueOf(startTimeRaw.split(":")[1].split("T")[0].substring(6,8)));
        else
            this.date = null;

        if(startTimeRaw.split(":")[1].split("T").length > 1)
            this.startTime = new Time(Integer.valueOf(startTimeRaw.split(":")[1].split("T")[1].substring(0,2)),Integer.valueOf(startTimeRaw.split(":")[1].split("T")[1].substring(2,4)));
        else
            this.startTime = null;

        if(endTimeRaw.split(":")[1].split("T").length > 1)
            this.endTime = new Time(Integer.valueOf(endTimeRaw.split(":")[1].split("T")[1].substring(0,2)),Integer.valueOf(endTimeRaw.split(":")[1].split("T")[1].substring(2,4)));
        else
            this.endTime = null;

        if(locationRaw.split(":").length > 1)
            this.location = locationRaw.split(":")[1];
        else
            this.location = "Not Specified";

        if(summaryRaw.split(":").length > 1)
            this.summary = summaryRaw.split(":")[1];
        else
            this.summary = "Not Specified";

    }

    /**
     * Return date
     * @return
     */
    public Date getDate() {
        return date;
    }

    /**
     * Return EndTime
     * @return
     */
    public Time getEndTime() {
        return endTime;
    }

    /**
     * Return StartTime
     * @return
     */
    public Time getStartTime() {
        return startTime;
    }

    /**
     * Return Location
     * @return
     */
    public String getLocation() {
        return location;
    }

    /**
     * Return Summary
     * @return
     */
    public String[] getSummary() {
        return summary.split("-");
    }

    /**
     * Return a string of the event
     * @return
     */
    @Override
    public String toString() {
        return "Event{" +
                "startTime=" + startTime +
                ", endTime=" + endTime +
                ", date=" + date +
                ", summary='" + summary + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
