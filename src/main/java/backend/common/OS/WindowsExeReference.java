package backend.common.OS;

import backend.common.JaroWinklerScore;
import nlp.Tokenizer;

import java.io.File;
import java.io.IOException;

public class WindowsExeReference extends ProgramReference {

    public WindowsExeReference(final File path, final WindowsShortcut shortcut){
        super();
        name = path.getName();
        target = shortcut.getRealFilename();
        args = shortcut.getCommandLineArguments();
    }

    public WindowsExeReference(final String text) throws IOException {
        super(text);
    }

    @Override
    public int start() {

        try {
            new ProcessBuilder(getTarget()).start();
            return 0; // All good
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return -1; // Something went wrong
    }

}
