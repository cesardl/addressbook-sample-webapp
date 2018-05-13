/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.dao.impl;

import org.sanmarcux.bd.ConnectionPool;
import org.sanmarcux.dao.UsuarioDAO;
import org.sanmarcux.domain.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author cesardl
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final Logger LOG = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    public Usuario getUsuario(Usuario usuario) {

        Usuario user = null;
        Connection connection = null;
        try {
            String sql = "SELECT usu_id, usu_usuario, usu_password"
                    + " FROM usuario WHERE usu_usuario = '" + usuario.getUsuUsuario() + "'"
                    + " AND usu_password = '" + usuario.getUsuPassword() + "';";

            LOG.debug(sql);

            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                user = new Usuario();
                user.setUsuId(resultSet.getInt(1));
                user.setUsuUsuario(resultSet.getString(2));
                user.setUsuPassword(resultSet.getString(3));
            }
        } catch (SQLException sqle) {
            LOG.error("Error en la consulta para buscar un usuario. Estado SQL: {}", sqle.getSQLState(), sqle);
        } finally {
            ConnectionPool.closeQuietly(connection);
        }
        return user;
    }
}
