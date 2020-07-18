package loadFile;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import connection.JDBCConnection;

public class ReadExcel {

	public static void readExcel(int idFile) throws IOException, ClassNotFoundException, SQLException
	{
		//connect database
		String url = "jdbc:mysql://localhost:3306/db_control";
		String user = "root";
		String pass = "";
		Connection connectionControl = JDBCConnection.getJDBCConnection(url,user,pass);
		Statement statementControl = connectionControl.createStatement();
		ResultSet resultSetLog = statementControl.executeQuery("SELECT * FROM log WHERE log.Status= 'ER' AND log.Id_File = "+idFile+" LIMIT 1;");
		while (resultSetLog.next()){
			String sourceFile = resultSetLog.getString(5);
			String fileStatus = resultSetLog.getString(3);
			ResultSet resultSet = statementControl.executeQuery("SELECT* FROM control WHERE control.Id_File= "+idFile);
			while (resultSet.next()){
				String destination = resultSet.getString(3);
				String username = resultSet.getString(4);
				String password = resultSet.getString(5);
				String fields = resultSet.getString(6);
				Connection connection = JDBCConnection.getJDBCConnection(destination, username, password);
				
				FileInputStream fileInputStream = new FileInputStream(sourceFile);
				XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				rowIterator.next();
				
				String sql = "INSERT INTO students (";
				String[] arFiels = fields.split("\\,");
				for (int i =0;i<arFiels.length;i++) {
					if(i==0) {
						sql+=arFiels[i];
					}else {
						sql+=","+arFiels[i];
					}
					
				}
				sql+=") VALUES (";
				for (int i =0;i<arFiels.length-1;i++) {
					if(i==0) {
						sql+="?";
					}else {
						sql+=",?";
					}
					
				}
				sql+=",'Null')";
				PreparedStatement statement2 = connection.prepareStatement(sql);
				
				while (rowIterator.hasNext()) {
					// lấy phần tử hiện tại
					Row nextRow = rowIterator.next();
					Iterator<Cell> cellIterator = nextRow.cellIterator();
					
					while (cellIterator.hasNext()) {
						Cell nextCell = cellIterator.next();
						
						int columnIndex = nextCell.getColumnIndex();
						
						switch (columnIndex) {
						case 1:
							int mssvInt = (int) nextCell.getNumericCellValue();
							String mssv =  ""+mssvInt;
							statement2.setString(1, mssv);
							break;
						case 2:
							String lastName = nextCell.getStringCellValue();
							statement2.setString(2, lastName);
							break;
						case 3:
							String firstName = nextCell.getStringCellValue();
							statement2.setString(3, firstName);
							break;
						case 4:
							Date dateOfBirth =  nextCell.getDateCellValue();
							statement2.setString(4, getString(dateOfBirth));
							break;
						case 5:
							String classID = nextCell.getStringCellValue();
							statement2.setString(5, classID);
							break;
						case 6:
							String className = nextCell.getStringCellValue();
							statement2.setString(6, className);
							break;
							
						case 7:
							int sdtInt = (int) nextCell.getNumericCellValue();
							String sdt = ""+sdtInt;
							statement2.setString(7, sdt);
							break;
						case 8:
							String email = nextCell.getStringCellValue();
							statement2.setString(8, email);
							break;
						case 9:
							String queQuan = nextCell.getStringCellValue();
							statement2.setString(9, queQuan);
							break;
						case 10:
							String note = "Null";
							
							statement2.setString(10, note);
							break;
						}
						
					}
					statement2.execute();
					
				}
				workbook.close();
				fileInputStream.close();
				statement2.close();
				connection.close();
				System.out.println("Done!");
			}
		}
		
	}
	public static String getString(Date d) {
		return new SimpleDateFormat("yyyy-MM-dd").format(d);
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		readExcel(1);
	}

}
