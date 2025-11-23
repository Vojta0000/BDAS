package upce.javafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionSingleton {
    private static ConnectionSingleton instance = null;
    public static ConnectionSingleton getInstance() {
        if (instance == null) {
            try {
                instance = new ConnectionSingleton();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    public Connection getConnection() throws SQLException {
        try {
            if (connection.isValid(2)) {
                return connection;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        connect();
        return connection;
    }
    private Connection connection;
    private ConnectionSingleton() throws SQLException {
        connect();
    }

    private void connect() throws SQLException {
        String url = "jdbc:oracle:thin:@//localhost:1521/";
        String service = "FREEPDB1";
        String user = "my_user";
        String password = "password_i_should_change";

        connection = DriverManager.getConnection(url + service, user, password);
        instance = this;
    }
}
