package backend.common.OS;

import java.io.File;
import java.io.IOException;

public class MacAppReference extends ProgramReference {

    public MacAppReference(final File file){
        name = file.getName();
        target = file.getAbsolutePath();
        args = null;
    }

    @Override
    public int start() {

        try {
            new ProcessBuilder("open -a \"" + getTarget() + "\"").start();
            return 0; // All good
        }

        catch (IOException e) {
            e.printStackTrace();
        }

        return -1; // Something went wrong
    }
}
