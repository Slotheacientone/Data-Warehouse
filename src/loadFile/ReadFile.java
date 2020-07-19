package loadFile;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import connection.JDBCConnection;

public class ReadFile {
	public static void readFile(int idFile) throws IOException, ClassNotFoundException, SQLException
	{
		//connect database
		String url = "jdbc:mysql://localhost:3306/db_control";
		String user = "root";
		String pass = "";
		Connection connectionControl = JDBCConnection.getJDBCConnection(url,user,pass);
		Statement statementControl = connectionControl.createStatement();
		//lấy file trong log theo idFile và có trạng thái ER
		ResultSet resultSetLog = statementControl.executeQuery("SELECT * FROM log WHERE log.Status= 'ER' AND log.Id_File = "+idFile+" LIMIT 1;");
		if(resultSetLog.next()){
			String sourceFile = resultSetLog.getString(5);
			String fileStatus = resultSetLog.getString(3);
			String typeFile = resultSetLog.getString(6);
			//lấy định loại file trong control vd sinhvien
			ResultSet resultSet = statementControl.executeQuery("SELECT* FROM control WHERE control.Id_File= "+idFile);
			if(resultSet.next()){
				String destination = resultSet.getString(3);
				String username = resultSet.getString(4);
				String password = resultSet.getString(5);
				String delimiter = resultSet.getString(8);
				//tất cả field của loại file
				String fields = resultSet.getString(6);
				if (typeFile.equals("xlsx")) {
					ReadExcel.readExcel(destination, username, password, fields, sourceFile);
				}else if(typeFile.equals("txt")){
					ReadTXT.readValuesTXT(destination, username, password, fields, sourceFile, delimiter);
				}
				
			}
		}
		
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		readFile(1);
	}
}
