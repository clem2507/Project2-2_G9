package backend;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Represents the base of any fallback interpreter.
 */
public interface FallbackInterpreter {

    // This method is supposed to receive a string (i.e. the query) and
    // return a string (i.e. the response)
    // How it works it is up to you :-)
    // This is supposed to work as a black box

    /**
     * Returns a predefined response and a confidence score.
     * @param query User query.
     * @return a Map.Entry object containing a response in the form of a string
     * and a confidence score in the form of a double ranging from 0 to 1.
     */
    Map.Entry<String, Double> processQuery(String query);
    // NOTE: IF no response then return null

    // This will get called whenever the user defines a new set
    // of predefined fallback patterns
    // You do not have to worry about who calls this method or
    // when this gets called

    /**
     * Reads and compiles a template file specified in newPath.
     * @param newPath path to template file.
     */
    boolean compileTemplate(String newPath);
    // NOTE: This can be called at any point during runtime

    /**
     * Returns the unique name of the interpreter, which should be listed in
     * InterpreterNames.
     * @return an enum of type InterpreterNames.
     */
    InterpreterNames getName();

    /**
     * Resets the state of the interpreter, forgetting user defined skills.
     */
    void reset();


}
