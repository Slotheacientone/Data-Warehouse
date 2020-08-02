package loadFile;

import connection.JDBCConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ReadFile {
    public static void readFile(int idFile) throws IOException, ClassNotFoundException, SQLException {
    	//check process
    	Connection connectionProcess = JDBCConnection.getConnection("db_control");
        Statement statementProcess = connectionProcess.createStatement();
        ResultSet resultSetProcess = statementProcess.executeQuery("SELECT * FROM process;");
        if (resultSetProcess.next()) {
            String status = resultSetProcess.getString(2);
            if(!status.equals("finished")) {
            	return;
            }
        }
        String sql2 = "UPDATE process SET process.status = 'pending' WHERE process.process_name ='loadfile';";
        statementProcess.executeUpdate(sql2);
        //loadfile
        Connection connection = JDBCConnection.getConnection("db_control");
        Statement statementControl = connection.createStatement();
        //lấy file trong log theo idFile và có trạng thái ER
        ResultSet resultSetLog = statementControl.executeQuery("SELECT * FROM log WHERE log.Status= 'ER' AND log.Id_File = " + idFile + " LIMIT 1;");
        if (resultSetLog.next()) {
            String sourceFile = resultSetLog.getString(5);
            String nameFile = resultSetLog.getString(4);
            String typeFile = resultSetLog.getString(6);
            String update="err";
            //lấy định loại file trong control vd sinhvien
            ResultSet resultSet = statementControl.executeQuery("SELECT* FROM config WHERE config.id= " + idFile);
            if (resultSet.next()) {
                String destination = resultSet.getString(3);
                //tất cả field của loại file
                String fields = resultSet.getString(4);
                String delimiter = resultSet.getString(6);
                if(idFile!=2) {
                	if (typeFile.equals("xlsx")) {
                		if(ReadExcel.readExcel(destination, fields, sourceFile,idFile)) {
                			update ="loaded";
                		}
                	}else if(typeFile.equals("txt")){
                		if(ReadTXT.readValuesTXT(destination,fields, sourceFile, delimiter,idFile)) {
                			update ="loaded";
                		}
                	}
                }else{
                	if (typeFile.equals("xlsx")) {
                		if(ReadExcelForMonHoc.readExcel(destination, fields, sourceFile,idFile)) {
                			update ="loaded";
                		}
                	}
//                	else if(typeFile.equals("txt")){
//                		if(ReadTXT.readValuesTXT(destination,fields, sourceFile, delimiter,idFile)) {
//                			update ="loaded";
//                		}
//                	}
                }
            }
            String sqlUpdateStatusLog ="UPDATE log SET log.Status = '"+update+"' WHERE log.File_Name='"+nameFile+"';";
            statementControl.execute(sqlUpdateStatusLog);
        }
        statementControl.close();
        connection.close();
        //update process status
        sql2 = "UPDATE process SET process.status = 'finished' WHERE process.process_name ='loadfile';";
        statementProcess.executeUpdate(sql2);
        statementProcess.close();
        connectionProcess.close();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
       for(String s :args)
       {
    	   int n = Integer.parseInt(s);
    	   readFile(n);
       }
    }
}
