import java.io.*;
import java.sql.*;

public class ImportDataFromCSV {
	public static void main(String[] args) {
		String jdbcURL = "jdbc:mysql://localhost:3306/destinationDB";
		String username = "root";
		String password = "root";

		String csvFilePath = "HOCVIEN_Export_2020-06-29_03-00-32.csv";

		int batchSize = 20;

		Connection connection = null;

		try {

			connection = DriverManager.getConnection(jdbcURL, username, password);
			connection.setAutoCommit(false);

			String sql = "INSERT INTO HOCVIEN (MAHV, HO, TEN, NGSINH, GIOITINH, NOISINH, MALOP) VALUES (?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement statement = connection.prepareStatement(sql);

			BufferedReader lineReader = new BufferedReader(new FileReader(csvFilePath));
			String lineText = null;

			int count = 0;

			lineReader.readLine(); // skip header line

			while ((lineText = lineReader.readLine()) != null) {
				String[] data = lineText.split(",");
				String MAHV = data[0];
				String HO = data[1];
				String TEN = data[2];
				String NGSINH = data[3];
				String GIOITINH = data[4];
				String NOISINH = data[5];
				String MALOP = data[6];

				statement.setString(1, MAHV);
				statement.setString(2, HO);
				statement.setString(3, TEN);
				statement.setString(4, NGSINH);
				statement.setString(5, GIOITINH);
				statement.setString(6, NOISINH);
				statement.setString(7, MALOP);

				statement.addBatch();

				if (count % batchSize == 0) {
					statement.executeBatch();
				}
			}

			lineReader.close();

			// thực hiện các truy vấn còn lại
			statement.executeBatch();

			connection.commit();
			connection.close();
			System.out.println("Import success!");

		} catch (IOException ex) {
			System.err.println(ex);
		} catch (SQLException ex) {
			ex.printStackTrace();

			try {
				connection.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
