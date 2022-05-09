package ua.com.cruises.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private ConnectionPool() {}

    private static Logger logger = LogManager.getLogger(ConnectionPool.class);

    public static Connection getConnection() {
        Context ctx;
        DataSource dataSource;
        Connection connection = null;

        try {
            ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/connectionpool");
            connection = dataSource.getConnection();
            logger.info("provided connection to DB");
        }
        catch (NamingException | SQLException e) {
            logger.error(e);
        }

        return connection;
    }

}
