package upce.javafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ConnectionSingleton {
    // Thread-safe lazy singleton for configuration only
    private static volatile ConnectionSingleton instance = null;

    public static ConnectionSingleton getInstance() {
        if (instance == null) {
            synchronized (ConnectionSingleton.class) {
                if (instance == null) {
                    instance = new ConnectionSingleton();
                }
            }
        }
        return instance;
    }

    private final String url;
    private final String service;
    private final String user;
    private final String password;

    private ConnectionSingleton() {
        this.url = "jdbc:oracle:thin:@//localhost:1521/";
        this.service = "FREEPDB1";
        this.user = "my_user";
        this.password = "password_i_should_change";
    }

    /**
     * Returns a brand-new JDBC connection each call. The caller must close it (use try-with-resources).
     * Transaction boundaries are thus isolated per usage; the default auto-commit=true applies
     * unless explicitly disabled by the caller.
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url + service, user, password);
    }
}
