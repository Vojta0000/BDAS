package upce.javafx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ConnectionSingleton {
    // Thread-safe lazy singleton
    private static volatile ConnectionSingleton instance = null;

    public static ConnectionSingleton getInstance() {
        if (instance == null) {
            synchronized (ConnectionSingleton.class) {
                if (instance == null) {
                    try {
                        instance = new ConnectionSingleton();
                    } catch (SQLException e) {
                        throw new RuntimeException("Failed to initialize ConnectionSingleton", e);
                    }
                }
            }
        }
        return instance;
    }

    // The physical JDBC connection that may be recreated as needed
    private volatile Connection physicalConnection;
    // A stable proxy object returned to callers; it never changes, so old references remain usable
    private final Connection proxyConnection;
    private final Object reconnectLock = new Object();

    private ConnectionSingleton() throws SQLException {
        connect();
        this.proxyConnection = createProxy();
    }

    public Connection getConnection() throws SQLException {
        // Always return the stable proxy that auto-reconnects and never actually closes
        ensureValid();
        return proxyConnection;
    }

    private void connect() throws SQLException {
        String url = "jdbc:oracle:thin:@//localhost:1521/";
        String service = "FREEPDB1";
        String user = "my_user";
        String password = "password_i_should_change";

        this.physicalConnection = DriverManager.getConnection(url + service, user, password);
    }

    private boolean isPhysicalValid() {
        try {
            return physicalConnection != null && !physicalConnection.isClosed() && physicalConnection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    private void ensureValid() throws SQLException {
        if (isPhysicalValid()) return;
        synchronized (reconnectLock) {
            if (!isPhysicalValid()) {
                // Attempt reconnect
                closeQuietly(physicalConnection);
                connect();
            }
        }
    }

    private Connection createProxy() {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();

                // Make close() a no-op so callers can't invalidate the shared connection reference
                if ("close".equals(name)) {
                    return null; // no-op
                }

                // isClosed() should reflect that the logical shared connection is usable
                if ("isClosed".equals(name)) {
                    return false; // the proxy is never considered closed
                }

                // Before any other call, ensure the physical connection is valid; reconnect if needed
                ensureValid();

                try {
                    return method.invoke(physicalConnection, args);
                } catch (Throwable t) {
                    // If we get an SQLException, try one transparent reconnect and retry once
                    if (t.getCause() instanceof SQLException || t instanceof SQLException) {
                        synchronized (reconnectLock) {
                            // Try to reconnect and retry once
                            closeQuietly(physicalConnection);
                            connect();
                            return method.invoke(physicalConnection, args);
                        }
                    }
                    throw t;
                }
            }
        };

        return (Connection) Proxy.newProxyInstance(
                Connection.class.getClassLoader(),
                new Class[]{Connection.class},
                handler
        );
    }

    private static void closeQuietly(Connection c) {
        if (c != null) {
            try { c.close(); } catch (Exception ignored) { }
        }
    }
}
