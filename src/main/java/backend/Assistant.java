package backend;

import domains.Location.FindMe;
import domains.SayThis;
import nlp.MatchedSequence;
import nlp.Tokenizer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Assistant {
    private Set<Domain> domains;
    private BlockingQueue<AssistantMessage> outputChannel;
    private Set<Thread> runningSkills;

    public Assistant(){
        domains = new HashSet<>();
        outputChannel = new LinkedBlockingQueue<>();
        runningSkills = new HashSet<>();

        addDomain(new SayThis());
        addDomain(new FindMe());
    }

    public void processQuery(final String query){
        assert !domains.isEmpty();
        Domain selectedDomain = null;
        MatchedSequence obtainedSequence = null;

        for(Domain d : domains){
            final MatchedSequence sequence = d.matchQuery(query);

            if(sequence != null && (obtainedSequence == null || sequence.useRatio() > obtainedSequence.useRatio())){
                selectedDomain = d;
                obtainedSequence = sequence;
            }

        }

        if(selectedDomain != null){
            System.out.println("Selected Domain: " + selectedDomain.getUniqueName());
            Skill skill = selectedDomain.dispatchSkill(obtainedSequence, outputChannel);
            Thread thread = new Thread(skill);
            thread.start();
            runningSkills.add(thread);
        }

        else{
            // In the future, here is where the assistant will invoke the fallback system with user defined responses
            System.out.println("Query not understood");
            pushMessage("Query not understood");
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

    public AssistantMessage getOutputOrContinue() throws InterruptedException {
        return outputChannel.poll(0, TimeUnit.MILLISECONDS);
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

}