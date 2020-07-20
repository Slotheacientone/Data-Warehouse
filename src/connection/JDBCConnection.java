package connection;

import java.sql.*;

public class JDBCConnection {
	public static Connection getConnection(String database) {
		try {
			String url = "jdbc:mysql://localhost:3306/";
			String user = "root";
			String password = "";
			url = url + database;
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

	public static ResultSet readTable(String database, String sql) throws SQLException {
		Connection connection = getConnection(database);
		Statement statement = connection.createStatement();
		return statement.executeQuery(sql);
	}

	public static boolean writeTable(String database, String sql) throws SQLException {
		Connection connection = getConnection(database);
		Statement statement = connection.createStatement();
		return statement.execute(sql);
	}
}
