package domains.Calendar;

import backend.*;
import nlp.MatchedSequence;
import nlp.NLPError;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;

public class Calendar extends Domain {

    private static int fromScale(String spec) throws NLPError {

        if(Collections.singletonList("am").contains(spec)){
            return 0;
        }

        if(Collections.singletonList("pm").contains(spec)){
            return 12;
        }

        if(!Collections.singletonList("am").contains(spec) && !Collections.singletonList("pm").contains(spec))
        {
            return 0;
        }

        throw new NLPError("Illegal time scale " + spec);
    }

    /**
     * This is the Calendar skill
     * It's connected with the UM calender and it has its own calendar
     * It's able to create events and in future phases also edit and delete them.
     */
    public Calendar()
    {
        super(DomainNames.Calendar);

        addPattern("<schedule, tasks, events, do, agenda> <...>");
        addPattern("<schedule, tasks, events, do, agenda> <#:4> <at> <@block, param:int> <@day_part, am, pm>");
        addPattern("<at> <@block, param:int> <@day_part, am, pm> <#:5> <schedule, tasks, events, do, agenda>");
        addPattern("<schedule, tasks, events, do, agenda> <#:4> <at> <@block, param:int>");
        addPattern("<at> <@block, param:int> <#:5> <schedule, tasks, events, do, agenda>");
        addPattern("<set calendar> <...>");
        addPattern("<reset calendar>");
        addPattern("<help calendar>");
        addPattern("<create event>");

        //TODO implement pattern parameter configuration for next phase
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
                    setCal();   //Reads the calendar.txt file to set up the link to download the UM calendar.
                } else if (sequence.getStringAt(0).toLowerCase().contains(("reset calendar").toLowerCase())) {
                    //Resets the calendar.txt file to set up the link to download the UM calendar.
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
                    //Allows the user to create events in the user calendar. Note that the user calendar is not the UM calendar.
                    pushMessage("Event created", MessageType.STRING);
                    String mac = getMacAddress();
                    File file;
                    file = new File("src/assets/ProjectData/Calendar/" + mac + ".txt");
                    try (PrintWriter writer = new PrintWriter(new FileWriter(file, true))) {
                        System.out.println("POPUP");
                        writer.println("BEGIN:VEVENT\n");
                        String eName = Popup.userInput("Event Name:").get();
                        String eDate = Popup.userInput("Date:").get();
                        String eStartTime = Popup.userInput("Start Time:").get();
                        String eEndTime = Popup.userInput("End Time:").get();
                        String eLocation = Popup.userInput("Location:").get();
                        writer.println("START:" + optimizeInput(eDate,false) + "T" + optimizeInput(eStartTime,false) + "\nEND:" + optimizeInput(eDate,false) + "T" + optimizeInput(eEndTime,false) + "\nNAME:" + eName + "\nLOCATION:" + eLocation);
                        writer.println("END:VEVENT\n\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(sequence.getStringAt(0).toLowerCase().contains(("help calendar").toLowerCase()))
                {
                    //Return messages to help the user using the skill.
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
                    //Checks if the UM calendar has been set up properly.
                    pushMessage("The calendar is not set.", MessageType.STRING);
                    pushMessage("Use command \"set calendar\" to set it", MessageType.STRING);
                    pushMessage("or use the command \"help calendar\"",  MessageType.STRING);
                    pushMessage("to receive help.",  MessageType.STRING);
                    setCal();
                }
                else {
                    //Returns the events of a determined day and if the user chooses to can return the events in hour blocks.
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

            /**
             * Sets up the UM calendar
             */
            public void setCal()
            {
                try {
                File file = new File("src/assets/ProjectData/Calendar/calendar.txt");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(optimizeInput(Popup.userInput("Insert here your link please:").get(), true));
                fileWriter.close();
                pushMessage("Done", MessageType.STRING);
                return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Due to the .ics standard we have to optimize the input when creating new events
             * @param s
             * @return
             */
            public String optimizeInput(String s, Boolean link)
            {
                String output = "";

                for(char e : s.toCharArray())
                {
                    if(!(e == ':' ||
                    e == '/' ||
                    e == ' ' ||
                    e == '-' ||
                    e == '_' ||
                    e == '.') && !link)
                    {
                        output += e;
                    }
                    else if(!(e == ' ') && link)
                    {
                        output += e;
                    }
                }
                if(output.length() < 4 && output.length() != 1)
                {
                    for (int i = output.length(); i < 5; i++) {
                        output += "0";
                    }
                }
                else if(output.length() == 1)
                {
                    String temp = "0";
                    output = temp += output;

                    for (int i = output.length(); i < 5; i++) {
                        output += "0";
                    }
                }
                return output;
            }

            /**
             * returns the USER calendar
             * @return
             */
            public File getCurrentUserCalendar()
            {
                File file;
                file = new File("src/assets/ProjectData/Calendar/" + getMacAddress() + ".txt");
                return file;
            }

            /**
             * Returns the user MAC address
             * @return
             */
            @SuppressWarnings("UnnecessaryLocalVariable")
            public String getMacAddress()
            {
                InetAddress localHost;
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
                return String.join("-", hexadecimal);
            }

            /**
             * Prints to screen and Event e
             * @param e
             */
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

            /**
             * This method is used to add or subtract days to a date
             * @param days
             * @return
             */
            public Date getDate(long days)
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                LocalDateTime tomo = now.plusDays(days);
                Date date = new Date(Integer.parseInt(dtf.format(tomo).split("-")[0]),Integer.parseInt(dtf.format(tomo).split("-")[1]),Integer.parseInt(dtf.format(tomo).split("-")[2]));
                System.out.println("AIM DATE = " + date.toString());
                return date;
            }

            /**
             * Returns the current date
             * @return
             */
            public Date getCurrentDate()
            {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime now = LocalDateTime.now();
                return new Date(Integer.parseInt(dtf.format(now).split("-")[0]),Integer.parseInt(dtf.format(now).split("-")[1]),Integer.parseInt(dtf.format(now).split("-")[2]));
            }

            /**
             * This is the method that checks if the UM calender is buffered.
             * @return
             */
            public boolean isCalendarBuffered()
            {
                try{
                    files = new FileInputStream("src/assets/ProjectData/Calendar/calendar.txt");
                    Scanner scanner = new Scanner(files);
                    if(!scanner.hasNextLine() || Objects.equals(scanner.nextLine(), ""))
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
