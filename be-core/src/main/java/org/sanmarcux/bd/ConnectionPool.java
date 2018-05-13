package org.sanmarcux.bd;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionPool {

    private static final Logger LOG = LoggerFactory.getLogger(ConnectionPool.class);

    private static DataSource dataSource;

    static {
        ResourceBundle rb = ResourceBundle.getBundle("org.sanmarcux.bd.jdbc");
        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setDriverClassName(rb.getString("jdbc.driverClassName"));
        basicDataSource.setUrl(rb.getString("jdbc.url"));
        basicDataSource.setUsername(rb.getString("jdbc.username"));
        basicDataSource.setPassword(rb.getString("jdbc.password"));

        basicDataSource.setValidationQuery("select 1");

        dataSource = basicDataSource;
    }

    public static Connection openConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException sqle) {
            LOG.error("Error al abrir la conexion", sqle);
            throw sqle;
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null) {
                connection.close();
            } else {
                LOG.warn("No hay una conexión abierta para cerrar");
            }
        } catch (SQLException e) {
            LOG.error("Error al cerrar la conexion", e);
        }
    }
}
