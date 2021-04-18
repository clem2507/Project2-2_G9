package backend;

import domains.Calendar.Calendar;
import domains.Leave;
import domains.FindMe;
import domains.OpenApplication;
import domains.Photo;
import domains.SayThis;
import domains.Search.SearchDomain;
import domains.SmallTalk;
import domains.FindWeather;
import nlp.MatchedSequence;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Assistant {
    private final String TEMPLATES_PATH = "src/assets/ProjectData/PatternTemplates/";

    private final Set<Domain> domains;
    private final BlockingQueue<AssistantMessage> outputChannel;
    private Set<Thread> runningSkills, backgroundSkills;
    private FallbackInterpreter[] interpreters;
    private String selectedInterpreter;

    public Assistant(){
        domains = new HashSet<>();
        outputChannel = new LinkedBlockingQueue<>();
        runningSkills = new HashSet<>();
        backgroundSkills = new HashSet<>();

        interpreters = new FallbackInterpreter[]{
                new RegexSkillsInterpreter()
        };
        selectedInterpreter = interpreters[0].getName().toString();

        addDomain(new SayThis());
        addDomain(new FindMe());
        addDomain(new FindWeather());
        addDomain(new Photo());
        addDomain(new OpenApplication());
        addDomain(new Leave());
        addDomain(new SmallTalk());
        addDomain(new Calendar());
        addDomain(new SearchDomain());

        // Here we load user defined templates from previous sessions.
        Arrays.stream(interpreters).forEach(this::loadTemplates);

    }

    private void loadTemplates(final FallbackInterpreter interpreter) {

        try {
            Path templatesFolder = Paths.get(TEMPLATES_PATH + interpreter.getName() + "/");
            Files.createDirectories(templatesFolder);

            Files.list(templatesFolder).forEach(p -> {
                String absolutePath = p.toAbsolutePath().toString();
                interpreter.compileTemplate(absolutePath);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Process a query.
     * NOTE: This is supposed to be executed on a single thread. In other words, there is no limit
     * to how many times this is called or where this is called, but it has to be called only on the
     * same thread each time. Normally, this thread would be the main thread - but it doesn't need
     * to be the main thread
     * @param query a string
     */
    synchronized public void processQuery(final String query){
        Domain selectedDomain = null; // Best domain matched so far
        MatchedSequence domainMatchSequence = null; // Best matched sequence so far
        Map.Entry<String, Double> bestInterpreterResponse = null; // Best response from custom skills

        for(Domain d : domains){ // Iterate ove the domains
            final MatchedSequence sequence = d.matchQuery(query); // Match the query with the 'best' pattern in the domain
            // NOTE: 'best' means that the domain passed the 'two questions test' and the information use ratio
            // (i.e. matched tokens / query length ratio) is the highest

            // If the sequence is a match and it is a better match than obtainedSequence or it is the first match
            if(sequence != null && (domainMatchSequence == null || sequence.useRatio() > domainMatchSequence.useRatio())){
                selectedDomain = d;
                domainMatchSequence = sequence;
            }

        }

        for(FallbackInterpreter interpreter : interpreters) {
            Map.Entry<String, Double> interpreterResponse = interpreter.processQuery(query);

            if(interpreterResponse != null
            && (bestInterpreterResponse == null || interpreterResponse.getValue() > bestInterpreterResponse.getValue())
            ) {
                bestInterpreterResponse = interpreterResponse;
            }

        }

        double domainConfidence = domainMatchSequence != null? domainMatchSequence.useRatio():0;
        double interpreterConfidence = bestInterpreterResponse != null? bestInterpreterResponse.getValue():0;

        System.out.println("Domain Confidence: " + domainConfidence);
        System.out.println("Interpreter Confidence: " + interpreterConfidence);

        final double MIN_CONFIDENCE_THRESHOLD = 0.0001;
        if(domainConfidence < MIN_CONFIDENCE_THRESHOLD && interpreterConfidence < MIN_CONFIDENCE_THRESHOLD) {
            pushMessage("I do not understand");
        }

        else {

            if (domainConfidence >= interpreterConfidence) {
                assert selectedDomain != null;
                Skill skill = selectedDomain.dispatchSkill(domainMatchSequence, outputChannel); // Declare skill

                // Send skill to run in the background
                Thread thread = new Thread(skill);
                thread.start();
                runningSkills.add(thread);
            } else {
                assert bestInterpreterResponse != null;
                String response = bestInterpreterResponse.getKey();
                pushMessage(response);
            }

        }

    }

    /**
     * Removes the skills that are no longer running. This has to be called periodically in the same thread
     * processQuery is called.
     */
    public void cleanSkillPool(){
        runningSkills = runningSkills.stream()
                .filter(Thread::isAlive)
                .collect(Collectors.toSet());
        backgroundSkills = backgroundSkills.stream()
                .filter(Thread::isAlive)
                .collect(Collectors.toSet());
    }

    /**
     * Tells all the running skills to stop and waits for them to stop. This has to be called only
     * before quitting the application. It can be called on a different thread from processQuery, but
     * only if you can ensure processQuery is not being called at the same time as this function.
     * Note: If this function is not called, there will be memory leaks after closing the app.
     */
    public void interruptAndWait(){
        runningSkills.forEach(Thread::interrupt);
        runningSkills.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        backgroundSkills.forEach(Thread::interrupt);
        backgroundSkills.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Returns and removes the oldest output message in the queue. If there is
     * no message, it waits. This can be called anywhere and
     * from any thread.
     * @return an AssistantMessage object.
     * @throws InterruptedException if the thread calling this function is interrupted
     */
    public AssistantMessage getOutputOrWait() throws InterruptedException {
        return outputChannel.take();
    }

    /**
     * Returns and removes the oldest output message in the queue, if and only if, there is a message
     * waiting. If there is none, it returns an empty Optional and continues execution - i.e. does not wait
     * @return Optional object
     * @throws InterruptedException if the thread calling this function is interrupted
     */
    public Optional<AssistantMessage> getOutputOrContinue() throws InterruptedException {
        return Optional.ofNullable(outputChannel.poll(0, TimeUnit.MILLISECONDS));
    }

    /**
     * Pushes a message into the output queue. This is thread safe and can be called from any
     * thread at any point
     * @param message String to push
     */
    public void pushMessage(final String message){

        try {
            outputChannel.put(new AssistantMessage(null, message, MessageType.STRING));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds a new domain to the set. This should only be called in the constructor.
     * @param domain a domain to add
     */
    private void addDomain(final Domain domain){
        assert !domains.contains(domain);
        assert domain.getUniqueName() != null;

        Optional<Skill> backgroundSkill = domain.backgroundSkill();

        if(backgroundSkill.isPresent()){
            Thread thread = new Thread(backgroundSkill.get());
            thread.start();
            backgroundSkills.add(thread);
        }

        domains.add(domain);
    }

    /**
     * Tells the fallback system that the user has defined a new set of responses in a file
     * identified by the path. This can be called from any thread at any point - i.e. thread safe
     * @param path String representing the new path
     */
    synchronized public void notifyOfNewPath(String path){
        Path source = Paths.get(path);
        Path destination = Paths.get(
                TEMPLATES_PATH + selectedInterpreter + "/" + source.getName(source.getNameCount() - 1).toString()
        );

        try {
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            Arrays.stream(interpreters)
                    .filter(i -> i.getName().toString().equals(selectedInterpreter))
                    .findFirst().orElseThrow()
                    .compileTemplate(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    synchronized public void forgetTemplates() {

        // A bit of a hack, but still readable -Dennis

        try {
            Files.list(Paths.get(TEMPLATES_PATH + selectedInterpreter + "/")).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        Arrays.stream(interpreters)
                .filter(i -> i.getName().toString().equals(selectedInterpreter))
                .findFirst()
                .orElseThrow()
                .reset();
    }

}