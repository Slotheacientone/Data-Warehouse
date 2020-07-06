package dataMover;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CopyData {
	
	private static String jdbcURL_1 = "jdbc:mysql://localhost:3306/sourceDB";
	private static String jdbcURL_2 = "jdbc:mysql://localhost:3306/destinationDB";
	private static String username = "root";
	private static String password = "root";

	public static void main(String[] args) {
		try (Connection connection1 = DriverManager.getConnection(jdbcURL_1, username, password);
				Connection connection2 = DriverManager.getConnection(jdbcURL_2, username, password);
				final Statement statement = connection1.createStatement();
				final PreparedStatement insertStatement = connection2
						.prepareStatement("insert into HOCVIEN2 values(?, ?, ?, ?, ?, ?, ?)")) {
			try (final ResultSet resultSet = statement
					.executeQuery("select MAHV, HO, TEN, NGSINH, GIOITINH, NOISINH, MALOP from HOCVIEN")) {
				while (resultSet.next()) {
					// Get the values from the table1 record
					final String mahv = resultSet.getString("MAHV");
					final String ho = resultSet.getString("HO");
					final String ten = resultSet.getString("TEN");
					final String ngaySinh = resultSet.getString("NGSINH");
					final String gioiTinh = resultSet.getString("GIOITINH");
					final String noiSinh = resultSet.getString("NOISINH");
					final String maLop = resultSet.getString("MALOP");

					// Insert a row with these values into table2
					insertStatement.clearParameters();
					insertStatement.setString(1, mahv);
					insertStatement.setString(2, ho);
					insertStatement.setString(3, ten);
					insertStatement.setString(4, ngaySinh);
					insertStatement.setString(5, gioiTinh);
					insertStatement.setString(6, noiSinh);
					insertStatement.setString(7, maLop);
					insertStatement.executeUpdate();
				}
				statement.close();
				connection1.close();
				connection2.close();
			} catch (SQLException e) {
				System.out.println("Datababse error:");
				e.printStackTrace();
			}
		} catch (SQLException e) {
			System.out.println("Datababse error:");
			e.printStackTrace();
		}
		System.out.println("Copy success!");
	}
}
