package download;

import connection.JDBCConnection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;

public class Scp {
    public static void main(String[] args) throws IOException, InterruptedException, SQLException {
        Connection connection = JDBCConnection.getConnection("db_control");
        Statement processStatement = connection.createStatement();
        ResultSet resultSetProcess = processStatement.executeQuery("SELECT status FROM process WHERE process_name = 'download'");
        resultSetProcess.next();
        if (resultSetProcess.getString("status").equals("pending")) {
            return;
        }
        resultSetProcess.close();
        processStatement.close();
        PreparedStatement processPreparedStatement = connection.prepareStatement("UPDATE `db_control`.`process` SET status=? WHERE (`process_name` = 'download');");
        processPreparedStatement.setString(1, "pending");
        processPreparedStatement.execute();
        String configID = args[0];
        Statement configStatement = connection.createStatement();
        ResultSet resultSet = configStatement.executeQuery("SELECT Regrex FROM config WHERE id=" + configID);
        resultSet.next();
        String regrex = resultSet.getString("Regrex");
        resultSet.close();
        configStatement.close();
        String[] cmd = {"src/download/ScpScript.zsh", regrex};
        ProcessBuilder processBuilder = new ProcessBuilder(cmd);
        Process process = processBuilder.start();
        process.waitFor();
        File tempFolder = new File("temp");
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
        for (File file : listFileTemp) {
            if (!listFileDownloaded.contains(file.getName())) {
                Files.copy(file.toPath(), new File("data/" + file.getName()).toPath());
                preparedStatementLog.setString(1, configID);
                preparedStatementLog.setString(2, "ER");
                preparedStatementLog.setString(3, file.getName());
                preparedStatementLog.setString(4, "data/" + file.getName());
                String[] temp = file.getName().split("\\.");
                preparedStatementLog.setString(5, temp[1]);
                preparedStatementLog.setString(6, new Timestamp(System.currentTimeMillis()) + "");
                preparedStatementLog.execute();
            }
        }
        preparedStatementLog.close();
        processPreparedStatement.setString(1, "finished");
        processPreparedStatement.execute();
        processPreparedStatement.close();
        connection.close();
    }
}
