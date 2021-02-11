package backend;

import domains.Location.FindMe;
import domains.OpenApplication;
import domains.Photo.Photo;
import domains.SayThis;
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
    private Set<Domain> domains;
    private BlockingQueue<AssistantMessage> outputChannel;
    private Set<Thread> runningSkills;
    private FallbackInterpreter fallback;

    public Assistant(){
        domains = new HashSet<>();
        outputChannel = new LinkedBlockingQueue<>();
        runningSkills = new HashSet<>();

        addDomain(new SayThis());
        addDomain(new FindMe());
        addDomain(new FindWeather());
        addDomain(new Photo());
        addDomain(new OpenApplication());
    }

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

        if(selectedDomain != null){ // If a domained matched
            Skill skill = selectedDomain.dispatchSkill(obtainedSequence, outputChannel); // Declare skill

            // Send skill to run in the background
            Thread thread = new Thread(skill);
            thread.start();
            runningSkills.add(thread);
        }

        else{
            pushMessage("Query not understood"); // Push message to the queue
        }

    }

    public void cleanSkillPool(){
        runningSkills = runningSkills.stream()
                .filter(Thread::isAlive)
                .collect(Collectors.toSet());
    }

    public void interruptAndWait(){
        runningSkills
                .forEach(Thread::interrupt);
        runningSkills.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    public AssistantMessage getOutputOrWait() throws InterruptedException {
        return outputChannel.take();
    }

    public Optional<AssistantMessage> getOutputOrContinue() throws InterruptedException {
        return Optional.ofNullable(outputChannel.poll(0, TimeUnit.MILLISECONDS));
    }

    public void pushMessage(final String message){

        try {
            outputChannel.put(new AssistantMessage(null, message));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addDomain(final Domain domain){
        assert !domains.contains(domain);
        assert domain.getUniqueName() != null;
        domains.add(domain);
    }

    public void removeDomain(final Domain domain){
        assert domains.contains(domain);
        domains.remove(domain);
    }

    public boolean hasDomain(final Domain domain){
        return domains.contains(domain);
    }

    public void notifyOfNewPath(String path){
        fallback.notifyNewPath(path);
    }

}