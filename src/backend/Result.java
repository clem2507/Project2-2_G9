package backend;

public class Result {
    private Skill sender;
    private String message;

    Result(Skill sender, String message){
        this.sender = sender;
        this.message = message;
    }

    /**
     * Return a reference to the skill that generated the output
     * @return reference to a skill
     */
    public Skill getSender(){
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
