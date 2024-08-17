package uoa.lavs.mainframe.simulator.failures;

import uoa.lavs.mainframe.simulator.IntermittentFailurePolicy;

import java.io.File;

public class FileExistsPolicy implements IntermittentFailurePolicy {

    private final String filePath;

    public FileExistsPolicy(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean canSend(boolean checkOnly) {
        File file = new File(filePath);
        return !file.exists();
    }
}
