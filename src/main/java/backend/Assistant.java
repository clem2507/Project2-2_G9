package backend;

import nlp.Tokenizer;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Assistant {
    private Set<Domain> skills;
    private BlockingQueue<AssistantOutput> assistantOutputs;
    private Set<Thread> runningTasks;

    public Assistant(){
        skills = new HashSet<>();
        assistantOutputs = new LinkedBlockingQueue<>();
        runningTasks = new HashSet<>();
    }

    /**
     * Push a query and process it
     * @param query
     */
    public void processQuery(final String query){
        System.out.println(Tokenizer.asTokenList(query));
        //TODO: Modify this function such that it supports
        //            1) Using a threshold to determine if no skill can handle the query
        //            2) Ask the user to rephrase if more than 1 skill can handle the query
        //  (note to self)
        //  -Dennis
        assert !skills.isEmpty();
        System.out.println(query);
        List<String> tokens = Arrays.stream(query.split("\\s+")).collect(Collectors.toList());
        Domain selectedSkill = this.skills.stream()
                .max(Comparator.comparingDouble(a -> a.weight(tokens)))
                .orElseThrow();
        Skill task = selectedSkill.createTask(tokens, assistantOutputs);
        Thread thread = new Thread(task);
        thread.start();
        runningTasks.add(thread);
    }

    /**
     * Dispose tasks that already finished
     */
    public void cleanTaskPool(){
        runningTasks = runningTasks.stream()
                .filter(Thread::isAlive)
                .collect(Collectors.toSet());
    }

    /**
     * Interrupt all tasks and wait
     */
    public void interruptAndWait(){
        runningTasks
                .forEach(Thread::interrupt);
        runningTasks.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Pull the oldest output and remove it, waits if empty
     * @return Result object
     */
    public AssistantOutput getOutputOrWait() throws InterruptedException {
        return assistantOutputs.take();
    }

    /**
     * Pull the oldest output and remove it, ignore if empty
     * @return Result if queue is not empty, null otherwise
     * @throws InterruptedException
     */
    public AssistantOutput getOutputOrContinue() throws InterruptedException {
        return assistantOutputs.poll(0, TimeUnit.MILLISECONDS);
    }

    public void pushMessage(final String message){

        try {
            assistantOutputs.put(new AssistantOutput(null, message));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add a skill
     * @param skill skill to add
     */
    public void addSkill(final Domain skill){
        assert !skills.contains(skill);
        assert skill.getUniqueName() != null;
        skills.add(skill);
    }

    /**
     * Removes a skill
     * @param skill skill to remove
     */
    public void removeSkill(final Skill skill){
        assert skills.contains(skill);
        skills.remove(skill);
    }

    /**
     * Checks if a skill exists
     * @param skill skill to check for
     * @return true if skill was found, false otherwise
     */
    public boolean hasSkill(final Skill skill){
        return skills.contains(skill);
    }

}