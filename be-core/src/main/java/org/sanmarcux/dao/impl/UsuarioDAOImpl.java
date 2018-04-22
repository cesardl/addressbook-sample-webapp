/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.sanmarcux.bd.ConnectionPool;
import org.sanmarcux.dao.UsuarioDAO;
import org.sanmarcux.domain.Usuario;

/**
 *
 * @author cesardl
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario getUsuario(Usuario usuario) {
        String sql = "SELECT usu_id, usu_usuario, usu_password"
                + " FROM usuario WHERE usu_usuario = '" + usuario.getUsuUsuario() + "'"
                + " AND usu_password = '" + usuario.getUsuPassword() + "';";

        Usuario user = null;
        Connection connection = null;
        try {
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
            System.err.println("Error en la consulta para buscar un usuario"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
        return user;
    }
}
