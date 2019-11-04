package org.sanmarcux.bd;

import org.apache.commons.dbcp.BasicDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ConnectionPool {

    private static DataSource dataSource;

    private ConnectionPool() {
    }

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
        return dataSource.getConnection();
    }
}
