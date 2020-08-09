package loadFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import connection.JDBCConnection;
import sendMail.SendMailSSL;

public class ReadCSV {
	public static boolean readValuesCSV(String destination, String fields, String sourceFile,int idFile){
		File file = new File(sourceFile);
		if (!file.exists()) {
			return false;
		}
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

		String sqlTruncate = "TRUNCATE TABLE "+table+";";
		Connection connection = JDBCConnection.getConnection(destination);
		PreparedStatement statement2 = null;
		try {
			statement2= connection.prepareStatement(sqlTruncate);
			statement2.executeUpdate();
			BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"));
			String line = bReader.readLine();
			// Kiểm tra xem tổng số field trong file có đúng format
			while ((line = bReader.readLine()) != null) {
				String[] arrValue = line.split(",");
				// file có ô stt nên fiels phải thêm 1
				if (arFiels.length + 1 == arrValue.length) {
					statement2 = connection.prepareStatement(sql2);
				} // file có ô stt và ghi chú ==null
				else if (arFiels.length == arrValue.length) {
					statement2 = connection.prepareStatement(sql);
				} else {
					continue;
				}
				for (int i = 1; i < arrValue.length; i++) {
					String value = "" + arrValue[i];
					statement2.setString(i, value);
				}
				statement2.execute();
			}
			bReader.close();
			statement2.close();
			connection.close();
			System.out.println("Done!");
			return true;
		} catch (NoSuchElementException | IOException |SQLException | NullPointerException e) {
			System.out.println("err");
			SendMailSSL.sendMail(e.toString());
			e.printStackTrace();
			return false;
		}

	}
}
