package connection;

import java.sql.*;

public class JDBCConnection {
	public static Connection getConnection(String database) {
		try {
			String url = "jdbc:mysql://localhost:3306/";
			String user = "root";
			String password = "$Ngay22031999";
			url = url + database;
			Class.forName("com.mysql.cj.jdbc.Driver");
			return DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
