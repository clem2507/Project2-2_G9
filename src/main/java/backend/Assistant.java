package backend;

import domains.Calendar.Calendar;
import domains.Leave;
import domains.Location.FindMe;
import domains.OpenApplication;
import domains.Photo.Photo;
import domains.SayThis;
import domains.SmallTalk;
import domains.Weather.FindWeather;
import nlp.MatchedSequence;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Assistant {
    private final Set<Domain> domains;
    private final BlockingQueue<AssistantMessage> outputChannel;
    private Set<Thread> runningSkills, backgroundSkills;
    private final FallbackInterpreter customFallback;

    public Assistant(){
        domains = new HashSet<>();
        outputChannel = new LinkedBlockingQueue<>();
        runningSkills = new HashSet<>();
        backgroundSkills = new HashSet<>();
        customFallback = new DummyFallback();

        addDomain(new SayThis());
        addDomain(new FindMe());
        addDomain(new FindWeather());
        addDomain(new Photo());
        addDomain(new OpenApplication());
        addDomain(new Leave());
        addDomain(new SmallTalk());
        addDomain(new Calendar());
    }

    /**
     * Process a query.
     * NOTE: This is supposed to be executed on a single thread. In other words, there is no limit
     * to how many times this is called or where this is called, but it has to be called only on the
     * same thread each time. Normally, this thread would be the main thread - but it doesn't need
     * to be the main thread
     * @param query a string
     */
    public void processQuery(final String query){
        Domain selectedDomain = null; // Best domain matched so far
        MatchedSequence obtainedSequence = null; // Best matched sequence so far

        for(Domain d : domains){ // Iterate ove the domains
            final MatchedSequence sequence = d.matchQuery(query); // Match the query with the 'best' pattern in the domain
            // NOTE: 'best' means that the domain passed the 'two questions test' and the information use ratio
            // (i.e. matched tokens / query length ratio) is the highest

            // If the sequence is a match and it is a better match than obtainedSequence or it is the first match
            if(sequence != null && (obtainedSequence == null || sequence.useRatio() > obtainedSequence.useRatio())){
                selectedDomain = d;
                obtainedSequence = sequence;
            }

        }

        if(selectedDomain != null){ // If a domain matched
            Skill skill = selectedDomain.dispatchSkill(obtainedSequence, outputChannel); // Declare skill

            // Send skill to run in the background
            Thread thread = new Thread(skill);
            thread.start();
            runningSkills.add(thread);
        }

        else{
            String customResponse;

            synchronized (customFallback) {
                customResponse = customFallback.processQuery(query);
            }

            if(customResponse != null){
                pushMessage(customResponse);
            }

            else{
                pushMessage("Query not understood"); // Push failure message to the queue
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
            outputChannel.put(new AssistantMessage(Optional.empty(), message, MessageType.STRING));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Adds a new domain to the set. This should only be called in the constructor.
     * @param domain
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
     * Removes a domain from the set. This should only be called in the same thread as
     * processQuery
     * @param domain
     */
    public void removeDomain(final Domain domain){
        assert domains.contains(domain);
        domains.remove(domain);
    }

    /**
     * Checks if a domain is already in the set. This should only be called
     * in the same thread as processQuery
     * @param domain
     * @return boolean - true if the domain is present, false otherwise
     */
    public boolean hasDomain(final Domain domain){
        return domains.contains(domain);
    }

    /**
     * Tells the fallback system that the user has defined a new set of responses in a file
     * identified by the path. This can be called from any thead at any point - i.e. thread safe
     * @param path String representing the new path
     */
    public void notifyOfNewPath(String path){

        synchronized (customFallback) {
            customFallback.notifyNewPath(path);
        }

    }

}