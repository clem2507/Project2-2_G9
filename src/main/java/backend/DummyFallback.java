package backend;

public class DummyFallback implements FallbackInterpreter {
    @Override
    public String processQuery(String query) {
        return null;
    }

    @Override
    public void notifyNewPath(String newPath) {

    }
}
