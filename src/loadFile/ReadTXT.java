package loadFile;

import connection.JDBCConnection;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class ReadTXT {
    public static boolean readValuesTXT(String destination, String fields,String sourceFile, String delimiter) throws SQLException {
        File file = new File(sourceFile);
        if (!file.exists()) {
            return false;
        }
        Connection connection = JDBCConnection.getConnection(destination);
        // tạo câu query
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
        sql += ",'Null')";
        PreparedStatement statement2 = connection.prepareStatement(sql);
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf8"));
            String line = bReader.readLine();
            // Kiểm tra xem tổng số field trong file có đúng format
//			if (new StringTokenizer(line, delimiter).countTokens() != (arFiels.length+1)) {
//				bReader.close();
//				return;
//			}
            while ((line = bReader.readLine()) != null) {
                String[] arrValue = line.split(delimiter);
                for (int i = 1; i < arrValue.length; i++) {
                    switch (i) {
                        case 1:

                            String mssv = "" + arrValue[i];
                            statement2.setString(1, mssv);
                            break;
                        case 2:
                            String lastName = "" + arrValue[i];
                            statement2.setString(2, lastName);
                            break;
                        case 3:
                            String firstName = "" + arrValue[i];
                            statement2.setString(3, firstName);
                            break;
                        case 4:
                            String dateOfBirth = "" + arrValue[i];
                            statement2.setString(4, dateOfBirth);
                            break;
                        case 5:
                            String classID = "" + arrValue[i];
                            statement2.setString(5, classID);
                            break;
                        case 6:
                            String className = "" + arrValue[i];
                            statement2.setString(6, className);
                            break;
                        case 7:
                            String sdt = "" + arrValue[i];
                            statement2.setString(7, sdt);
                            break;
                        case 8:
                            String email = "" + arrValue[i];
                            statement2.setString(8, email);
                            break;
                        case 9:
                            String queQuan = "" + arrValue[i];
                            statement2.setString(9, queQuan);
                            break;
//					case 10:
//						System.out.println("abc");
//						String note = "Null";
//					if(nextCell.getCellType()!=null)
//					{
//						note += nextCell.getStringCellValue();
//					}else {
//						note += "Null";
//					}
//						statement2.setString(10, note);
//						break;
                    }
                }
                statement2.execute();
            }
            bReader.close();
            System.out.println("Done!");
        } catch (NoSuchElementException | IOException e) {
            e.printStackTrace();
            return false;
        }
        statement2.close();
        connection.close();
        return true;
    }
}
