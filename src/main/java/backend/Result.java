package backend;

public class Result {
    private final SkillDispatcher sender;
    private final String message;

    public Result(SkillDispatcher sender, String message){
        this.sender = sender;
        this.message = message;
    }

    /**
     * Return a reference to the skill that generated the output
     * @return reference to a skill
     */
    public SkillDispatcher getSender(){
        return sender;
    }

    /**
     * Return message
     * @return a string
     */
    public String getMessage(){
        return message;
    }

}
