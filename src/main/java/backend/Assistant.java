package backend;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Assistant {
    private Set<SkillDispatcher> skills;
    private BlockingQueue<Result> results;
    private Set<Thread> runningTasks;

    public Assistant(){
        skills = new HashSet<>();
        results = new LinkedBlockingQueue<>();
        runningTasks = new HashSet<>();
    }

    /**
     * Push a query and process it
     * @param query
     */
    public void processQuery(String query){
        //TODO: Modify this function such that it supports
        //            1) Using a threshold to determine if no skill can handle the query
        //            2) Ask the user to rephrase if more than 1 skill can handle the query
        assert !skills.isEmpty();
        List<String> tokens = Arrays.stream(query.split("\\s+")).collect(Collectors.toList());
        SkillDispatcher selectedSkill = this.skills.stream()
                .max(Comparator.comparingDouble(a -> a.weight(tokens)))
                .orElseThrow();
        Skill task = selectedSkill.createTask(tokens, results);
        Thread thread = new Thread(task);
        thread.start();
        runningTasks.add(thread);
    }

    /**
     * Dispose tasks that already finished
     */
    void cleanTaskPool(){
        runningTasks = runningTasks.stream()
                .filter(Thread::isAlive)
                .collect(Collectors.toSet());
    }

    /**
     * Interrupt all tasks and wait
     */
    void interruptAndWait(){
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
    Result getOutputOrWait() throws InterruptedException {
        return results.take();
    }

    /**
     * Pull the oldest output and remove it, ignore if empty
     * @return Result if queue is not empty, null otherwise
     * @throws InterruptedException
     */
    Result getOutputOrContinue() throws InterruptedException {
        return results.poll(100, TimeUnit.MILLISECONDS);
    }

    void pushMessage(String message){

        try {
            results.put(new Result(null, message));
        }

        catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Add a skill
     * @param skill skill to add
     */
    void addSkill(SkillDispatcher skill){
        assert !skills.contains(skill);
        assert skill.getName() != null;
        skills.add(skill);
    }

    /**
     * Removes a skill
     * @param skill skill to remove
     */
    void removeSkill(Skill skill){
        assert skills.contains(skill);
        skills.remove(skill);
    }

    /**
     * Checks if a skill exists
     * @param skill skill to check for
     * @return true if skill was found, false otherwise
     */
    boolean hasSkill(Skill skill){
        return skills.contains(skill);
    }

}
