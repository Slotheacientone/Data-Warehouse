package connection;

import java.sql.*;

public class JDBCConnection {
	public static Connection getJDBCConnection(String url, String user, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
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
	public static ResultSet readTable(String sql) throws SQLException {
		Connection connection = getJDBCConnection("jdbc:mysql://localhost:3306/db_control", "root", "$Ngay22031999");
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sql);
		return resultSet;
	}

	public static boolean writeTable(String sql) throws SQLException {
		boolean result = false;
		Connection connection = getJDBCConnection("jdbc:mysql://localhost:3306/db_control", "root", "$Ngay22031999");
		Statement statement = connection.createStatement();
		result = statement.execute(sql);
		return result;
	}
}
