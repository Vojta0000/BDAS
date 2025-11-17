package upce.javafx;

import java.sql.*;

/**
 * Docker compose see here
 * https://hub.docker.com/r/gvenzl/oracle-free
 */
public class DbConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:oracle:thin:@//localhost:1521/";
        String service = "FREEPDB1";
        String user = "my_user";
        String password = "password_i_should_change";

        return DriverManager.getConnection(url + service, user, password);
    }

    public static String getNow() {
        try (Connection conn = getConnection()) {
            String sql = "SELECT SYSDATE FROM dual";
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    return rs.getString(1);
                }
            }

            return sql;

        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
