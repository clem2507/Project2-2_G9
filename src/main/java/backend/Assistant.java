package backend;

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
    }

    public void processQuery(final String query){
        System.out.println(Tokenizer.asTokenList(query));
        //TODO: Modify this function such that it supports
        //            1) Using a threshold to determine if no skill can handle the query
        //            2) Ask the user to rephrase if more than 1 skill can handle the query
        //  (note to self)
        //  -Dennis
        assert !domains.isEmpty();
        System.out.println(query);
        List<String> tokens = Arrays.stream(query.split("\\s+")).collect(Collectors.toList());
        Domain selectedDomain = this.domains.stream()
                .max(Comparator.comparingDouble(a -> a.weight(tokens)))
                .orElseThrow();
        Skill skill = selectedDomain.dispatchSkill(tokens, outputChannel);
        Thread thread = new Thread(skill);
        thread.start();
        runningSkills.add(thread);
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