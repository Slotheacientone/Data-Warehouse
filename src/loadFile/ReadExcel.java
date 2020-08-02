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

	public static void readExcel(String destination, String fields, String sourceFile)
			{
		// tạo câu query1 cho những câu không có ghi chú và query 2 cho câu có ghi chú
		String sql = "INSERT INTO students (";
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
			FormulaEvaluator formula = workbook.getCreationHelper().createFormulaEvaluator();
			Iterator<Row> rowIterator = sheet.iterator();
			rowIterator.next();
			while (rowIterator.hasNext()) {
				// lấy phần tử hiện tại
				Row nextRow = rowIterator.next();
				int last = nextRow.getLastCellNum();
				if (last == arFiels.length + 1) {
					
						statement2 = connection.prepareStatement(sql2);
					 
				} else if (last == arFiels.length) {
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
							int mssvInt = (int) nextCell.getNumericCellValue();
							String mssv = "" + mssvInt;
							statement2.setString(columnIndex, mssv);
							break;
						case STRING:
							String lastName = nextCell.getStringCellValue();
							statement2.setString(columnIndex, lastName);
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
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Err!");
			e.printStackTrace();
			
		}catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Err!");
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Err!");
			e.printStackTrace();
		}
		

	}

	public static String getString(Date d) {
		return new SimpleDateFormat("yyyy-MM-dd").format(d);
	}
}
