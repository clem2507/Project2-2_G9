package domains.Calendar;

import backend.*;
import nlp.MatchedSequence;
import nlp.NLPError;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class Calendar extends Domain {

    private static int fromScale(String spec) throws NLPError {

        if(Arrays.asList("am").contains(spec)){
            return 0;
        }

        if(Arrays.asList("pm").contains(spec)){
            return 12;
        }

        if(!Arrays.asList("am").contains(spec) && !Arrays.asList("pm").contains(spec))
        {
            return 0;
        }

        throw new NLPError("Illegal time scale " + spec);
    }


    public Calendar()
    {
        super(DomainNames.Calendar);

        addPattern("<schedule> <...>");
        addPattern("<schedule> <#:4> <at> <@block, param:int> <@day_part, am, pm>");
        addPattern("<at> <@block, param:int> <@day_part, am, pm> <#:5> <schedule>");
        addPattern("<schedule> <#:4> <at> <@block, param:int>");
        addPattern("<at> <@block, param:int> <#:5> <schedule>");
        addPattern("<do> <...>");
        addPattern("<set calendar> <...>");
        addPattern("<reset calendar>");
        addPattern("<help calendar>");
        addPattern("<create event>");

       /* // Patterns for the calendar -Dennis
        String base = "<schedule, agenda, things to do, events, chores, tasks> <#:1>";

        // i.e. "schedule for monday" (schedule for the nearest incoming monday
        addPattern(base + " <param:day>");

        // i.e. "agenda for today" (schedule for this current day)
        addPattern(base + " <today, now, this day>");

        // i.e. "things to do on 12/03/2021" (schedule for a specific date)
        addPattern(base + " <param:int> <\\, /> <param:int> <\\, /> <param:int>");*/
    }
    @Override
    public Skill dispatchSkill(MatchedSequence sequence, BlockingQueue<AssistantMessage> outputChannel) {

        Optional<Integer> waitTimeSlotIndex = sequence.getSlotIndex("@block");
        Optional<Integer> timeScaleSlotIndex = sequence.getSlotIndex("@day_part");

        int block = -1, part = 0; // Scale defaults to 1 - It shouldn't matter anyways

        try {
            block = waitTimeSlotIndex.isPresent()? sequence.getIntAt(waitTimeSlotIndex.get()):-1; // Default to no wait time

            if(timeScaleSlotIndex.isPresent()){
                part = fromScale(sequence.getStringAt(timeScaleSlotIndex.get()));
            }


        }catch (NLPError e)
        {
            e.printStackTrace();
        }


        final int finalBlock = block + part;


        return new Skill(this, outputChannel) {

            public FileInputStream files;

            @Override
            public void run() {


                if (sequence.getStringAt(0).toLowerCase().contains(("set calendar").toLowerCase())) {
                    setCal();
                } else if (sequence.getStringAt(0).toLowerCase().contains(("reset calendar").toLowerCase())) {

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

                } else if (sequence.getStringAt(0).toLowerCase().contains(("create event").toLowerCase())) {
                    pushMessage("Event created", MessageType.STRING);
                    String mac = getMacAddress();
                    File file = null;
                    file = new File("src/assets/ProjectData/Calendar/" + mac + ".txt");
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                        System.out.println("POPUP");
                        writer.println("BEGIN:VEVENT\n");
                        String eName = Popup.userInput("Event Name:").get();
                        String eDate = Popup.userInput("Date:").get();
                        String eStartTime = Popup.userInput("Start Time:").get();
                        String eEndTime = Popup.userInput("End Time:").get();
                        String eLocation = Popup.userInput("Location:").get();
                        if (eName.equals(null) || eDate.equals(null))
                            return;
                        writer.println("START:" + optimizeInput(eDate) + "T" + optimizeInput(eStartTime) + "\nEND:" + optimizeInput(eDate) + "T" + optimizeInput(eEndTime) + "\nNAME:" + eName + "\nLOCATION:" + eLocation);
                        writer.println("END:VEVENT\n\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    pushMessage("you can also add after the schedule command", MessageType.STRING);
                    pushMessage("at time am/pm so fe schedule... at 5 pm or", MessageType.STRING);
                    pushMessage("in 24h time at 17", MessageType.STRING);




                    return;
                }
                if(!isCalendarBuffered()){
                    pushMessage("The calendar is not set.", MessageType.STRING);
                    pushMessage("Use command \"set calendar\" to set it", MessageType.STRING);
                    pushMessage("or use the command \"help calendar\"",  MessageType.STRING);
                    pushMessage("to receive help.",  MessageType.STRING);
                    setCal();
                }
                else {
                    if(sequence.getStringAt(1).toLowerCase().contains(("tomorrow").toLowerCase())){
                        try {
                            File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                            Scanner reader = new Scanner(file);
                            String link = reader.nextLine();
                            reader.close();
                            System.out.println(link);
                            CalendarReader calendarReader = new CalendarReader(new URL(link),getCurrentUserCalendar());
                            Date currentDate = getDate(1);
                            boolean free = true;
                            for(Event e : calendarReader.getEvents())
                            {
                                System.out.println(e.toString());
                                if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()) && e.getBlockOutput(finalBlock))
                                {
                                    printEvent(e);
                                    free = false;
                                }
                                else if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()) && e.getBlockOutput(finalBlock))
                                {
                                    printEvent(e);
                                    free = false;
                                }
                            }
                            if(free && finalBlock == -1) pushMessage("Your day is free, enjoy!",MessageType.STRING);
                            else if(free) pushMessage("You are free at " + finalBlock + ", enjoy!",MessageType.STRING);
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
                            CalendarReader calendarReader = new CalendarReader(new URL(link),getCurrentUserCalendar());
                            Date currentDate = getCurrentDate();
                            boolean free = true;
                            for(Event e : calendarReader.getEvents())
                            {
                                System.out.println(e.toString());
                                if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()) && e.getBlockOutput(finalBlock))
                                {
                                    printEvent(e);
                                    free = false;
                                }
                                else if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()) && e.getBlockOutput(finalBlock))
                                {
                                    printEvent(e);
                                    free = false;
                                }
                            }
                            if(free && finalBlock == -1) pushMessage("Your day is free, enjoy!",MessageType.STRING);
                            else if(free) pushMessage("You are free at " + finalBlock + ", enjoy!",MessageType.STRING);
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
                            CalendarReader calendarReader = new CalendarReader(new URL(link),getCurrentUserCalendar());
                            Date currentDate = new Date(Integer.parseInt(sequence.getStringAt(1).split(" - ")[0]),Integer.parseInt(sequence.getStringAt(1).split(" - ")[1]),Integer.parseInt(sequence.getStringAt(1).split(" - ")[2]));
                            boolean free = true;
                            for(Event e : calendarReader.getEvents())
                            {
                                System.out.println(e.toString());
                                if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()) && e.getBlockOutput(finalBlock))
                                {
                                    printEvent(e);
                                    free = false;
                                }
                                else if((currentDate.getYear() == e.getDate().getYear()) && (currentDate.getMonth() == e.getDate().getMonth()) && (currentDate.getDay() == e.getDate().getDay()) && e.getBlockOutput(finalBlock))
                                {
                                    printEvent(e);
                                    free = false;
                                }
                            }
                            if(free && finalBlock == -1) pushMessage("Your day is free, enjoy!",MessageType.STRING);
                            else if(free) pushMessage("You are free at " + finalBlock + ", enjoy!",MessageType.STRING);
                        } catch (MalformedURLException | FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        pushMessage("Format is incorrect use command \"help calendar\"", MessageType.STRING);
                    }
                }
            }

            public void setCal()
            {
                try {
                File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(clearLink(Popup.userInput("Insert here your link please:").get()));
                fileWriter.close();
                pushMessage("Done", MessageType.STRING);
                return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public String optimizeInput(String s)
            {
                String output = "";

                for(char e : s.toCharArray())
                {
                    if(!(e == ':' ||
                    e == '/' ||
                    e == ' ' ||
                    e == '-' ||
                    e == '_' ||
                    e == '.'))
                    {
                        output += e;
                    }
                }
                return output;
            }

            public File getCurrentUserCalendar()
            {
                File file = null;
                file = new File("src/assets/ProjectData/Calendar/" + getMacAddress() + ".txt");
                return file;
            }

            public String getMacAddress()
            {
                InetAddress localHost = null;
                byte[] hardwareAddress = null;
                try {
                    localHost = InetAddress.getLocalHost();
                    NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
                    hardwareAddress = ni.getHardwareAddress();
                } catch (UnknownHostException | SocketException e) {
                    e.printStackTrace();
                }
                String[] hexadecimal = new String[hardwareAddress.length];
                for (int i = 0; i < hardwareAddress.length; i++) {
                    hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
                }
                String macAddress = String.join("-", hexadecimal);
                return macAddress;
            }

            public void printEvent(Event e)
            {
                pushMessage("You have:",MessageType.STRING);
                for(String s : e.getSummary())
                {
                    pushMessage(s + "\n",MessageType.STRING);
                }
                pushMessage("From " + e.getStartTime() + " until " + e.getEndTime(),MessageType.STRING);
                pushMessage("At " + e.getLocation(), MessageType.STRING);
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
                    Scanner scanner = new Scanner(files);
                    if(!scanner.hasNextLine() || scanner.nextLine() == "")
                    {
                        return false;
                    }
                }catch (IOException e){
                    return false;
                }
                return true;
            }
        };
    }
}
