package download;

import java.io.IOException;

public class Scp {
    public static void main(String[] args) throws IOException {
        String cmd = "src/download/ScpScript.zsh";
        Process process = Runtime.getRuntime().exec(cmd);

    }
}
