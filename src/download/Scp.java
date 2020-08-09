package download;

import connection.JDBCConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;

public class Scp {
    public static void main(String[] args) throws SQLException {
        //get connection
        Connection connection = JDBCConnection.getConnection("db_control");
        //check process status
        Statement processStatement = connection.createStatement();
        ResultSet resultSetProcess = processStatement.executeQuery("SELECT status FROM process WHERE process_name = 'download'");
        resultSetProcess.next();
        if (resultSetProcess.getString("status").equals("pending")) {
            System.out.println(resultSetProcess.getString("status"));
            resultSetProcess.close();
            processStatement.close();
            connection.close();
            return;
        }
        resultSetProcess.close();
        processStatement.close();
        //update process status to pending
        PreparedStatement processPreparedStatement = connection.prepareStatement("UPDATE `db_control`.`process` SET status=? WHERE (`process_name` = 'download');");
        processPreparedStatement.setString(1, "pending");
        processPreparedStatement.execute();
        processPreparedStatement.close();
        //read config
        String configID = args[0];
        Statement configStatement = connection.createStatement();
        ResultSet resultSet = configStatement.executeQuery("SELECT Regrex FROM config WHERE id=" + configID);
        resultSet.next();
        String regrex = resultSet.getString("Regrex");
        resultSet.close();
        configStatement.close();
        // create folder temp if not exist
        File tempFolder = new File("/home/slo/DataWarehouse/temp");
        if (!tempFolder.exists()) {
            tempFolder.mkdir();
        }
        //download file
        try {
            String[] cmd = {"/home/slo/DataWarehouse/ScpScript.zsh", regrex};
            ProcessBuilder processBuilder = new ProcessBuilder(cmd);
            Process process = processBuilder.start();
            process.waitFor();
        } catch (InterruptedException | IOException e) {
            System.out.println(e.getMessage());
            PreparedStatement processPreparedStatement1 = connection.prepareStatement("UPDATE `db_control`.`process` SET status=? WHERE (`process_name` = 'download');");
            processPreparedStatement1.setString(1, "finished");
            processPreparedStatement1.execute();
            processPreparedStatement1.close();
            connection.close();
        }
        //check if file downloaded exist in log
        File[] listFileTemp = tempFolder.listFiles();
        ArrayList<String> listFileDownloaded = new ArrayList<>();
        Statement logStatement = connection.createStatement();
        ResultSet resultSetLog = logStatement.executeQuery("SELECT File_Name FROM log WHERE Id_File=" + configID);
        while (resultSetLog.next()) {
            listFileDownloaded.add(resultSetLog.getString("File_Name"));
        }
        resultSetLog.close();
        logStatement.close();
        PreparedStatement preparedStatementLog = connection.prepareStatement("INSERT INTO `db_control`.`log` (Id_File, Status, File_Name, Source_File, Type_File, Time) VALUES (?,?,?,?,?,?)");
        File dataFolder = new File("/home/slo/DataWarehouse/data");
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        for (File file : listFileTemp) {
            if (!listFileDownloaded.contains(file.getName())) {
                try {
                    //if file does not exist in log copy to folder data
                    Files.copy(file.toPath(), new File("/home/slo/DataWarehouse/data/" + file.getName()).toPath());
                    preparedStatementLog.setString(1, configID);
                    preparedStatementLog.setString(2, "ER");
                    preparedStatementLog.setString(3, file.getName());
                    preparedStatementLog.setString(4, "/home/slo/DataWarehouse/data/" + file.getName());
                    String[] temp = file.getName().split("\\.");
                    preparedStatementLog.setString(5, temp[1]);
                    preparedStatementLog.setString(6, new Timestamp(System.currentTimeMillis()) + "");
                    preparedStatementLog.execute();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        preparedStatementLog.close();
        PreparedStatement processPreparedStatement1 = connection.prepareStatement("UPDATE `db_control`.`process` SET status=? WHERE (`process_name` = 'download');");
        processPreparedStatement1.setString(1, "finished");
        processPreparedStatement1.execute();
        processPreparedStatement1.close();
        connection.close();
    }
}
