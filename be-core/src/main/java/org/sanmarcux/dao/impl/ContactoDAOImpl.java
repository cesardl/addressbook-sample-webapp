package org.sanmarcux.dao.impl;

import org.sanmarcux.bd.ConnectionPool;
import org.sanmarcux.dao.ContactoDAO;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.domain.Usuario;
import org.sanmarcux.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ContactoDAOImpl implements ContactoDAO {

    private static final Logger LOG = LoggerFactory.getLogger(ContactoDAOImpl.class);

    public List<Contacto> listarContactos(final int usuId, final Usuario.Role role) {
        String sql = "SELECT con_id, con_codigo, con_nombres, con_telefono, con_email, con_cumpleanos FROM contacto";

        if (Usuario.Role.USER.equals(role)) {
            sql = sql + " WHERE usu_id = ?";
        }

        LOG.debug(sql);

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (Usuario.Role.USER.equals(role)) {
                ps.setInt(1, usuId);
            }

            return listContacts(ps);
        } catch (SQLException e) {
            LOG.error("Error en obtener la lista de contactos: {}. Estado SQL:{}", e.getMessage(), e.getSQLState(), e);
            return Collections.emptyList();
        }
    }

    public List<Contacto> listarContactos(final int usuId, final Usuario.Role role, final String dato) {
        String sql = "SELECT con_id, con_codigo, con_nombres, con_telefono, con_email, con_cumpleanos FROM contacto" +
                " WHERE con_nombres LIKE ?";

        if (Usuario.Role.USER.equals(role)) {
            sql = sql + " AND usu_id = ?";
        }

        LOG.debug(sql);

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, "%".concat(dato).concat("%"));

            if (Usuario.Role.USER.equals(role)) {
                ps.setInt(2, usuId);
            }

            return listContacts(ps);
        } catch (SQLException e) {
            LOG.error("Error en la consulta para buscar varios contactos por su nombre: {}. Estado SQL:{}", e.getMessage(), e.getSQLState(), e);
            return Collections.emptyList();
        }
    }

    private List<Contacto> listContacts(final PreparedStatement ps) throws SQLException {
        try (ResultSet resultSet = ps.executeQuery()) {

            List<Contacto> listContacts = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String codigo = resultSet.getString(2);
                String nombre = resultSet.getString(3);
                String telefono = resultSet.getString(4);
                String email = resultSet.getString(5);
                Date cumpleanos = resultSet.getDate(6);

                listContacts.add(new Contacto(id, codigo, nombre, telefono, email, cumpleanos));
            }

            return listContacts;
        }
    }

    public Contacto seleccionarContacto(final int idContacto) {
        String sql = "SELECT con_id, con_codigo, con_nombres, con_telefono,con_avatar, con_email, con_cumpleanos "
                + "FROM contacto WHERE con_id = ?";

        LOG.debug(sql);

        Contacto contacto = null;

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idContacto);

            try (ResultSet resultSet = ps.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt("con_id");
                    String codigo = resultSet.getString("con_codigo");
                    String nombre = resultSet.getString("con_nombres");
                    String telefono = resultSet.getString("con_telefono");
                    Blob avatar = resultSet.getBlob("con_avatar");
                    String email = resultSet.getString("con_email");
                    Date cumpleanos = resultSet.getDate("con_cumpleanos");

                    contacto = new Contacto(id, codigo, nombre, telefono, avatar, email, cumpleanos);
                }
            }
        } catch (SQLException e) {
            contacto = new Contacto();
            LOG.error("Error en la consulta para buscar un contacto por Id: {}. Estado SQL:{}", e.getMessage(), e.getSQLState(), e);
        }
        return contacto;
    }

    public Blob seleccionarAvatarContacto(final int idContacto) {
        String sql = "SELECT con_avatar FROM contacto WHERE con_id = ?";

        LOG.debug(sql);

        Blob avatar = null;
        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idContacto);

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    avatar = resultSet.getBlob("con_avatar");
                }
            }
        } catch (SQLException e) {
            LOG.error("Error en la consulta para buscar el avatar de un contacto. Estado SQL: {}", e.getSQLState(), e);
        }
        return avatar;
    }

    public String generarCodigoContacto() {
        String sql = "SELECT sp_genera_codigo()";

        LOG.debug(sql);

        String generatedCode = "";

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    generatedCode = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error en la generación de código del contacto. Estado SQL: {}", e.getSQLState(), e);
        }
        return generatedCode;
    }

    public void insertarContacto(Contacto contacto) {
        String sql = "INSERT INTO contacto(con_codigo, con_nombres, con_telefono,"
                + " con_avatar, con_email, con_cumpleanos, usu_id)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?)";

        LOG.debug(sql);

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, contacto.getConCodigo());
            ps.setString(2, contacto.getConNombres());
            ps.setString(3, contacto.getConTelefono());
            ps.setBlob(4, contacto.getConAvatar());
            ps.setString(5, contacto.getConEmail());
            ps.setDate(6, Utilities.fromUtilDateToSQLDate(contacto.getConCumpleanos()));
            ps.setInt(7, contacto.getUsuId());

            int result = ps.executeUpdate();

            LOG.debug("Total de registros afectados {}", result);
        } catch (SQLException e) {
            LOG.error("Error en la consulta para insertar un contacto: {}. Estado SQL:{}", e.getMessage(), e.getSQLState(), e);
        }
    }

    public void actualizarContacto(final Contacto contacto) {
        String sql = "UPDATE contacto SET con_codigo = ?, con_nombres = ?, con_telefono = ?, con_avatar = ?, con_email = ?, con_cumpleanos = ? WHERE con_id = ?";

        LOG.debug(sql);

        try (Connection connection = ConnectionPool.openConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, contacto.getConCodigo());
            ps.setString(2, contacto.getConNombres());
            ps.setString(3, contacto.getConTelefono());
            ps.setBlob(4, contacto.getConAvatar());
            ps.setString(5, contacto.getConEmail());
            ps.setDate(6, Utilities.fromUtilDateToSQLDate(contacto.getConCumpleanos()));
            ps.setInt(7, contacto.getConId());

            int result = ps.executeUpdate();

            LOG.debug("Total de registros afectados {}", result);
        } catch (SQLException e) {
            LOG.error("Error en la consulta para actualizar un contacto: {}. Estado SQL:{}", e.getMessage(), e.getSQLState(), e);
        }
    }

    public void eliminarContacto(final int idContacto) {
        String sql = "DELETE FROM contacto WHERE con_id = ?";

        LOG.debug(sql);

        try (Connection connection = ConnectionPool.openConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, idContacto);

            int result = ps.executeUpdate();

            LOG.debug("Total de registros afectados: {}", result);
        } catch (SQLException e) {
            LOG.error("Error en la consulta para eliminar un contacto: {}. Estado SQL:{}", e.getMessage(), e.getSQLState(), e);
        }
    }
}
