package org.sanmarcux.bd;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javax.sql.DataSource;
import org.apache.commons.dbcp.BasicDataSource;

public class ConnectionPool {

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

    public static Connection openConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException sqle) {
            System.err.println("Error al abrir la conexion " + sqle.getMessage());
            return null;
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            connection.close();
        } catch (SQLException sqle) {
            System.err.println("Error al cerrar la conexion " + sqle.getMessage());
        }
    }
}
