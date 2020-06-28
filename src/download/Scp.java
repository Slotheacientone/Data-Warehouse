package download;

import java.io.*;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;

public class Scp {
    public static void main(String[] args) throws IOException, InterruptedException {
        String cmd = "src/download/ScpScript.zsh";
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        Process process = processBuilder.start();
        process.waitFor();
        File tempFolder = new File("temp");
        File[] listFileTemp = tempFolder.listFiles();
        BufferedReader logReader = new BufferedReader(new FileReader("src/download/download.log"));
        String log;
        ArrayList<String> listFileDownloaded = new ArrayList<>();
        PrintWriter logWriter = new PrintWriter(new FileWriter("src/download/download.log", true));
        while ((log = logReader.readLine()) != null) {
            String[] string = log.split(" ");
            listFileDownloaded.add(string[2]);
        }
        for (File file : listFileTemp) {
            if (!listFileDownloaded.contains(file.getName())) {
                Files.copy(file.toPath(), new File("data/" + file.getName()).toPath());
                logWriter.println(new Timestamp(System.currentTimeMillis()) + " " + file.getName());
            }
        }
        logWriter.close();

    }
}
