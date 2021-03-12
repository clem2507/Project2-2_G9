package domains.Calendar;

import backend.*;
import nlp.MatchedSequence;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class Calendar extends Domain {
    public Calendar()
    {
        super(DomainNames.Calendar);

        addPattern("<schedule> <...>");
        //addPattern("<do> <...>"); I believe this pattern is not being used at all. - Dennis
        addPattern("<set calendar> <...>");
        addPattern("<reset calendar>");
        addPattern("<help calendar>");

        // Patterns for the calendar -Dennis
        String base = "<schedule, agenda, things to do, events, chores, tasks> <#:1>";

        // i.e. "schedule for monday" (schedule for the nearest incoming monday
        addPattern(base + " <param:day>");

        // i.e. "agenda for today" (schedule for this current day)
        addPattern(base + " <today, now, this day>");

        // i.e. "things to do on 12/03/2021" (schedule for a specific date)
        addPattern(base + " <param:int> <\\, /> <param:int> <\\, /> <param:int>");
    }
    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {
        return new Skill(this, outputChannel) {

            public FileInputStream files;

            @Override
            public void run() {

                if(sequence.getStringAt(0).toLowerCase().contains(("set calendar").toLowerCase()))
                {
                    try {
                        File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write(clearLink(sequence.getStringAt(1)));
                        fileWriter.close();
                        pushMessage("Done", MessageType.STRING);
                        return;
                        //TODO fix lower-case input link!
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(sequence.getStringAt(0).toLowerCase().contains(("reset calendar").toLowerCase()))
                {

                    try {
                        File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                        FileWriter fileWriter = new FileWriter(file);
                        fileWriter.write("");
                        fileWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pushMessage("Done", MessageType.STRING);
                    return;

                }
                else if(sequence.getStringAt(0).toLowerCase().contains(("help calendar").toLowerCase()))
                {
                    pushMessage("To get your timetable link", MessageType.STRING);
                    pushMessage("1) Go to the UM student portal", MessageType.STRING);
                    pushMessage("2) Go to the My timetable tab", MessageType.STRING);
                    pushMessage("3) Select calendar and scroll down", MessageType.STRING);
                    pushMessage("4) Click Agenda connect link", MessageType.STRING);
                    pushMessage("5) Open general timetables", MessageType.STRING);
                    pushMessage("6) Click connect calendar (top right)", MessageType.STRING);
                    pushMessage("7) Select \"Other\"", MessageType.STRING);
                    pushMessage("8) Click next and copy the link", MessageType.STRING);
                    pushMessage("To use the calendar", MessageType.STRING);
                    pushMessage("To get your schedule of", MessageType.STRING);
                    pushMessage("today: \"schedule today\"", MessageType.STRING);
                    pushMessage("tomorrow: \"schedule tomorrow\"", MessageType.STRING);
                    pushMessage("date: \"schedule yyyy-mm-dd\"", MessageType.STRING);




                    return;
                }
                if(!isCalendarBuffered()){
                    pushMessage("The calendar is not set.", MessageType.STRING);
                    pushMessage("Use command \"set calendar\" to set it", MessageType.STRING);
                    pushMessage("or use the command \"help calendar\"",  MessageType.STRING);
                    pushMessage("to receive help.",  MessageType.STRING);
                }
                else {
                    if(sequence.getStringAt(1).toLowerCase().contains(("tomorrow").toLowerCase())){
                        try {
                            File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                            Scanner reader = new Scanner(file);
                            String link = reader.nextLine();
                            reader.close();
                            System.out.println(link);
                            CalendarReader calendarReader = new CalendarReader(new URL(link));
                            Date currentDate = getDate(1);
                            boolean free = true;
                            for(Event e : calendarReader.getEvents())
                            {
                                System.out.println(e.toString());
                                if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()))
                                {
                                    pushMessage("You have:",MessageType.STRING);
                                    pushMessage(e.getSummary()[0] + " " + e.getSummary()[1],MessageType.STRING);
                                    pushMessage(e.getSummary()[2],MessageType.STRING);
                                    pushMessage("From " + e.getStartTime() + " until " + e.getEndTime(),MessageType.STRING);
                                    pushMessage("At " + e.getLocation(), MessageType.STRING);
                                    free = false;
                                }
                            }
                            if(free) pushMessage("Your day is free, enjoy!",MessageType.STRING);
                        } catch (MalformedURLException | FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(sequence.getStringAt(1).toLowerCase().contains(("today").toLowerCase())){
                        try {
                            File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                            Scanner reader = new Scanner(file);
                            String link = reader.nextLine();
                            reader.close();
                            System.out.println(link);
                            CalendarReader calendarReader = new CalendarReader(new URL(link));
                            Date currentDate = getCurrentDate();
                            boolean free = true;
                            for(Event e : calendarReader.getEvents())
                            {
                                System.out.println(e.toString());
                                if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()))
                                {
                                    pushMessage("You have:",MessageType.STRING);
                                    pushMessage(e.getSummary()[0] + " " + e.getSummary()[1],MessageType.STRING);
                                    pushMessage(e.getSummary()[2],MessageType.STRING);
                                    pushMessage("From " + e.getStartTime() + " until " + e.getEndTime(),MessageType.STRING);
                                    pushMessage("At " + e.getLocation(), MessageType.STRING);
                                    free = false;
                                }
                            }
                            if(free) pushMessage("Your day is free, enjoy!",MessageType.STRING);
                        } catch (MalformedURLException | FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else if(sequence.getStringAt(1).toLowerCase().contains((" - ").toLowerCase())){
                        try {
                            File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                            Scanner reader = new Scanner(file);
                            String link = reader.nextLine();
                            reader.close();
                            System.out.println(link);
                            CalendarReader calendarReader = new CalendarReader(new URL(link));
                            Date currentDate = new Date(Integer.parseInt(sequence.getStringAt(1).split(" - ")[0]),Integer.parseInt(sequence.getStringAt(1).split(" - ")[1]),Integer.parseInt(sequence.getStringAt(1).split(" - ")[2]));
                            boolean free = true;
                            for(Event e : calendarReader.getEvents())
                            {
                                System.out.println(e.toString());
                                if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()))
                                {
                                    pushMessage("You have:",MessageType.STRING);
                                    pushMessage(e.getSummary()[0] + " " + e.getSummary()[1],MessageType.STRING);
                                    pushMessage(e.getSummary()[2],MessageType.STRING);
                                    pushMessage("From " + e.getStartTime() + " until " + e.getEndTime(),MessageType.STRING);
                                    pushMessage("At " + e.getLocation(), MessageType.STRING);
                                    free = false;
                                }
                            }
                            if(free) pushMessage("Your day is free, enjoy!",MessageType.STRING);
                        } catch (MalformedURLException | FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        pushMessage("Format is incorrect use command \"help calendar\"", MessageType.STRING);
                    }
                }
            }

            public Date getDate(long days)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime tomo = now.plusDays(days);
                Date date = new Date(Integer.parseInt(dtf.format(tomo).split("-")[0]),Integer.parseInt(dtf.format(tomo).split("-")[1]),Integer.parseInt(dtf.format(tomo).split("-")[2]));
                System.out.println("AIM DATE = " + date.toString());
                return date;
            }

            public Date getCurrentDate()
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                return new Date(Integer.parseInt(dtf.format(now).split("-")[0]),Integer.parseInt(dtf.format(now).split("-")[1]),Integer.parseInt(dtf.format(now).split("-")[2]));
            }

            public String clearLink(String input)
            {
                String output = "";
                for(Character c : input.toCharArray())
                {
                    if(!c.equals(' '))
                    {
                        output += c;
                    }
                }
                return output;
            }

            public boolean isCalendarBuffered()
            {
                try{
                    files = new FileInputStream("src/assets/ProjectData/Calendar/calendar.txt");
                }catch (IOException e){
                    return false;
                }
                return true;
            }
        };
    }
}
