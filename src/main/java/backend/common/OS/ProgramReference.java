package backend.common.OS;

import backend.common.JaroWinklerScore;

import java.io.File;
import java.io.IOException;

public abstract class ProgramReference {
    protected String name, target, args;
    private final String DEFAULT_SEPARATOR = "--->";

    public ProgramReference(){
        target = "";
        name = "";
        args = null;
    }

    public ProgramReference(final String text) throws IOException {
        String[] params = text.split(DEFAULT_SEPARATOR);

        if(params.length > 3){
            throw new IOException("Program reference string " + text + " does not follow proper format");
        }

        name = params[0];
        target = params[1];
        args = params.length > 2? params[2]:null;
    }

    /**
     * Returns the full path to the targeted program (i.e. *.exe, *.app, etc)
     * @return a String representing the full path without command arguments
     */
    public String getTargetOnly(){
        return target;
    }

    /**
     * Returns the full path to the targeted program (i.e. *.exe, *.app, etc)
     * @return a String representing the full path with command arguments
     */
    public String getTarget(){

        if(args == null){
            return target;
        }

        return target + " " + args;
    }

    /**
     * Returns the file name of the link (i.e. *.lnl on Windows, *.app on MAC, etc)
     * @return a String representing the file name
     */
    public String getName(){
        return name;
    }

    /**
     * Tries running the referenced program
     * @return int representing the outcome. If no issues, returns 0
     */
    public abstract int start();

    /**
     * Returns a number between 0 and 1 (inclusive) representing how similar is the name to
     * the program reference - this is used for imperfect file search
     * @param name the program we are looking for
     * @return double representing the similarity
     */
    public double computeSimilarity(String name){
        return JaroWinklerScore.compute(getName().toLowerCase(), name.toLowerCase());
    }

    public boolean isBroken(){
        return !(new File(getTargetOnly())).exists();
    }

    @Override
    public String toString() {
        return getName() + DEFAULT_SEPARATOR + getTarget() + (args != null? DEFAULT_SEPARATOR + args:"");
    }
}
