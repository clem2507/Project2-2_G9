package backend;

public interface FallbackInterpreter {

    // This method is supposed to receive a string (i.e. the query) and
    // return a string (i.e. the response)
    // How it works it is up to you :-)
    // This is supposed to work as a black box
    String processQuery(String query);

    // This will get called whenever the user defines a new set
    // of predefined fallback patterns
    // You do not have to worry about who calls this method or
    // when this gets called
    void notifyNewPath(String newPath);

}
