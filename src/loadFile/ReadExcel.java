package loadFile;

import connection.JDBCConnection;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class ReadExcel {

	public static boolean readExcel(String destination, String fields, String sourceFile,int idFile)
			{
		// tạo câu query1 cho những câu không có ghi chú và query 2 cho câu có ghi chú
		String table = null;
		if(idFile==1) {
			table = "students";
		}else if(idFile==2){
			table = "monhoc";
		}else if(idFile ==3) {
			table = "lophoc";
		}else if(idFile==4) {
			table ="dangky";
		}
		String sql = "INSERT INTO "+table+" (";
		String[] arFiels = fields.split("\\,");
		for (int i = 0; i < arFiels.length; i++) {
			if (i == 0) {
				sql += arFiels[i];
			} else {
				sql += "," + arFiels[i];
			}
		}
		sql += ") VALUES (";
		for (int i = 0; i < arFiels.length - 1; i++) {
			if (i == 0) {
				sql += "?";
			} else {
				sql += ",?";
			}
		}
		String sql2 = sql;
		sql2 += ",?)";
		sql += ",'Null')";

		Connection connection = JDBCConnection.getConnection(destination);
		PreparedStatement statement2 = null;

		// lấy dữ liệu file.xlsx
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(sourceFile);
			// đưa fis về dạng file excel
			XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
			 
			// đọc sheet thứ nhất
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				// lấy phần tử hiện tại
				Row nextRow = rowIterator.next();
				// last = vị trí cell cuối cùng
				int last = nextRow.getLastCellNum();
				//file có ô stt nên fiels phải thêm 1
				if (arFiels.length+1==last ) {
						statement2 = connection.prepareStatement(sql2);
					 
				}// không thêm 1 vì không đọc ô ghi chú
				else if (arFiels.length== last) {
					statement2 = connection.prepareStatement(sql);
				} else {
					continue;
				}
				Iterator<Cell> cellIterator = nextRow.cellIterator();
				int count = 0;
				while (cellIterator.hasNext()) {
					Cell nextCell = cellIterator.next();
					CellType cellType = nextCell.getCellType();
					int columnIndex = nextCell.getColumnIndex();
					if (columnIndex != 0) {
						count++;
						switch (cellType) {
						case NUMERIC:
							int valueInt = (int) nextCell.getNumericCellValue();
							String value = "" + valueInt;
							statement2.setString(columnIndex, value);
							break;
						case STRING:
							String valueString = nextCell.getStringCellValue();
							statement2.setString(columnIndex, valueString);
							break;
						default:
							statement2.setString(columnIndex, "Null");
							break;
						}
					}

				}
				if (count + 1 == last) {
					statement2.execute();
				}
			}
			workbook.close();
			fileInputStream.close();
			statement2.close();
			connection.close();
			System.out.println("Done!");
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Err!");
			e.printStackTrace();
			return false;
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Err!");
			e.printStackTrace();
			return false;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Err!");
			
			e.printStackTrace();
			return false;
		}
		

	}

	public static String getString(Date d) {
		return new SimpleDateFormat("yyyy-MM-dd").format(d);
	}
}
