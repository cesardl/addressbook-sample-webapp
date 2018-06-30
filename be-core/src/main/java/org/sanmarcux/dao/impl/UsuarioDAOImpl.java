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

import java.sql.*;

/**
 * @author cesardl
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    private static final Logger LOG = LoggerFactory.getLogger(UsuarioDAOImpl.class);

    public Usuario getUsuario(final Usuario usuario) {
        String sql = "SELECT usu_id, usu_usuario, usu_role FROM usuario WHERE usu_usuario = ? AND usu_password = ?";

        LOG.debug(sql);

        Usuario user = null;

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsuUsuario());
            ps.setString(2, usuario.getUsuPassword());

            try (ResultSet resultSet = ps.executeQuery()) {

                if (resultSet.next()) {
                    user = new Usuario();
                    user.setUsuId(resultSet.getInt(1));
                    user.setUsuUsuario(resultSet.getString(2));
                    user.setRole(Usuario.Role.valueOf(resultSet.getString(3)));
                }
            }
        } catch (SQLException sqle) {
            LOG.error("Error en la consulta para buscar un usuario. Estado SQL: {}", sqle.getSQLState(), sqle);
        }

        return user;
    }

    @Override
    public void actualizarUltimoAcceso(final int usuId) {
        String sql = "UPDATE usuario SET last_login = ? WHERE usu_id = ?";

        LOG.debug(sql);

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, usuId);

            int result = ps.executeUpdate();

            LOG.debug("Total de registros afectados: {}", result);
        } catch (SQLException sqle) {
            LOG.error("Error en la consulta para buscar un usuario. Estado SQL: {}", sqle.getSQLState(), sqle);
        }
    }
}
