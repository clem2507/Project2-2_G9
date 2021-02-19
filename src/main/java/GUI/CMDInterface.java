package GUI;

import backend.Assistant;
import backend.AssistantMessage;

import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * This class is just for testing, runs the digital assistant in the console
 */
public class CMDInterface {
    private static final Assistant assistant = new Assistant();
    private static boolean running = true;
    private static final String EXIT_CODE = "EXIT";

    public static void main(String[] args) throws InterruptedException {
        Scanner inputScanner = new Scanner(System.in);
        Thread outputReader = new Thread(new Runnable() {
            @Override
            public void run() {

                while (running){

                    try {
                        Optional<AssistantMessage> output = assistant.getOutputOrContinue();
                        output.ifPresent(assistantMessage -> System.out.println(assistantMessage.toString()));
                        assistant.cleanSkillPool();
                    }

                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        outputReader.start();

        System.out.println("PROGRAM STARTED");

        while (running){
            String userInput = inputScanner.nextLine();

            if(userInput.equals(EXIT_CODE)){
                running = false;
                break;
            }

            assistant.processQuery(userInput);
        }

        outputReader.join();
        assistant.interruptAndWait();
    }

}
