import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

public class ReadExcel {

	public static void readExcel() throws IOException, ClassNotFoundException, SQLException
	{
		//connect database
		Class.forName("com.mysql.jdbc.Driver");
        Connection connectionControl = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_control","root", "");
        Statement statementControl = connectionControl.createStatement();
        ResultSet resultSet = statementControl.executeQuery("SELECT* FROM control");
        while (resultSet.next()){
            String source = resultSet.getString(2);
            String destination = resultSet.getString(3);
            String username = resultSet.getString(7);
            String password = resultSet.getString(8);
            
            Connection connection = DriverManager.getConnection(destination, username, password);
            FileInputStream fileInputStream = new FileInputStream(source);
//		FileInputStream fileInputStream = new FileInputStream("D:\\CNTT\\HK8\\Data Warehouse\\sinhvien_chieu_nhom14.xlsx");
		XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        rowIterator.next();
        
//        String sql = "INSERT INTO students (Mssv, Last_name, First_name,Date_of_birth, Class_ID, Class_name, Sdt, Email, QueQuan) VALUES (?,?,?,?,?,?,?,?,?)";
        String sql = "INSERT INTO students (Mssv, Last_name, First_name,Date_of_birth, Class_ID, Class_name, Sdt, Email, QueQuan, Note) VALUES (?,?,?,?,?,?,?,?,?,'abc')";
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
                        int mssv =  (int) nextCell.getNumericCellValue();
//                        System.out.print(mssv+"\t");
                        statement2.setInt(1, mssv);
                        break;
                    case 2:
                        String lastName = nextCell.getStringCellValue();
//                        System.out.print(lastName+"\t");
                        statement2.setString(2, lastName);
                        break;
                    case 3:
                        String firstName = nextCell.getStringCellValue();
//                        System.out.print(firstName+"\t");
                        statement2.setString(3, firstName);
                        break;
                    case 4:
                        Date dateOfBirth =  nextCell.getDateCellValue();
//                        System.out.print(getString(dateOfBirth)+"\t");
                        statement2.setString(4, getString(dateOfBirth));
                        break;
                    case 5:
                        String classID = nextCell.getStringCellValue();
//                        System.out.print(classID+"\t");
                        statement2.setString(5, classID);
                        break;
                    case 6:
                        String className = nextCell.getStringCellValue();
//                        System.out.print(className+"\t");
                        statement2.setString(6, className);
                        break;
                    
                    case 7:
                        String sdt = nextCell.getStringCellValue();
//                        System.out.print(sdt+"\t");
                        statement2.setString(7, sdt);
                        break;
                    case 8:
                        String email = nextCell.getStringCellValue();
//                        System.out.print(email+"\t");
                        statement2.setString(8, email);
                        break;
                    case 9:
                        String queQuan = nextCell.getStringCellValue();
//                        System.out.print(queQuan+"\t");
                        statement2.setString(9, queQuan);
                        break;
                    case 10:
                        String note = "abc";
//                        System.out.print(note+"\t");
//                       if (nextCell.getStringCellValue()!=null)
//                        {
//                        	note=nextCell.getStringCellValue();
//                        }
                        
                       statement2.setString(10, note);
                       break;
                }

            }
//            System.out.println();
            statement2.execute();
        }
            workbook.close();
            fileInputStream.close();
            statement2.close();
            connection.close();
            System.out.println("Done!");
        }
	}
	public static String getString(Date d) {
		return new SimpleDateFormat("yyyy-MM-dd").format(d);
	}
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		readExcel();

	}

}
