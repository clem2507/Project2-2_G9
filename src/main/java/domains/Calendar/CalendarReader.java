package domains.Calendar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class CalendarReader {

    public ArrayList<Event> events = new ArrayList<>();

    /**
     * Gets from Maastricht University timetable url the events data and creates
     * events by scraping the ics file.
     * To get the UM time table url go to Student Portal -> My timetable / calendar -> scroll down and click Agenda connect manual ->
     * follow the instructions.
     * @param url
     */
    public CalendarReader(URL url, File personalCalendar) {
        Scanner myReader = null;
        try {
            myReader = new Scanner(url.openStream());
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (data.equals("BEGIN:VEVENT")) {

                    String dumpLine = myReader.nextLine();
                    String line = null;
                    String startTime = myReader.nextLine();
                    String endTime = myReader.nextLine();
                    String summary = myReader.nextLine();
                    String location = null;
                    while (!(line = myReader.nextLine()).split(":")[0].equals("LOCATION")) {
                        summary += line;
                    }

                    location = line;


                    Event event = new Event(startTime, endTime, summary, location);
                    if (!(event.getSummary().equals("Not Specified") ||
                            event.getEndTime() == null ||
                            event.getStartTime() == null ||
                            event.getDate() == null))
                        events.add(event);


                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Scanner myReaderB = null;
            myReaderB = new Scanner(personalCalendar);
            System.out.println(personalCalendar.getName());
            while (myReaderB.hasNextLine()) {
                String data = myReaderB.nextLine();
                if (data.equals("BEGIN:VEVENT")) {

                    String dumpLine = myReaderB.nextLine();
                    String startTime = myReaderB.nextLine();
                    String endTime = myReaderB.nextLine();
                    String summary = myReaderB.nextLine();
                    String location = myReaderB.nextLine();



                    Event event = new Event(startTime, endTime, summary, location);

                    events.add(event);
                    System.out.println("EDDED" + event.toString());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        }
    }
        /**
     * Returns an arraylist with the calendar events
     * @return
     */
    public ArrayList<Event> getEvents() {
        return events;
    }
}
