package org.sanmarcux.dao.impl;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sanmarcux.bd.ConnectionPool;
import org.sanmarcux.dao.ContactoDAO;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.util.Utilities;

public class ContactoDAOImpl implements ContactoDAO {

    @Override
    public List<Contacto> listarContactos(int usuId) {
        String sql = "SELECT con_id, con_codigo, con_nombres, con_telefono, con_email, con_cumpleanos "
                + "FROM contacto WHERE usu_id = " + usuId;
        ArrayList<Contacto> listContacts = new ArrayList<Contacto>();
        Connection connection = null;

        try {
            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String codigo = resultSet.getString(2);
                String nombre = resultSet.getString(3);
                String telefono = resultSet.getString(4);
                String email = resultSet.getString(5);
                Date cumpleanos = resultSet.getDate(6);

                listContacts.add(new Contacto(id, codigo, nombre, telefono, email, cumpleanos));
            }
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para listar todos los contactos"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
        return listContacts;
    }

    @Override
    public List<Contacto> listarContactos(String dato, int usuId) {
        ArrayList<Contacto> listContacts = new ArrayList<Contacto>();
        String sql = "SELECT con_id, con_codigo, con_nombres, con_telefono, con_email, con_cumpleanos"
                + " FROM contacto WHERE usu_id = " + usuId + " AND con_nombres LIKE '%" + dato + "%'";
        Connection connection = null;

        try {
            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String codigo = resultSet.getString(2);
                String nombre = resultSet.getString(3);
                String telefono = resultSet.getString(4);
                String email = resultSet.getString(5);
                Date cumpleanos = resultSet.getDate(6);

                listContacts.add(new Contacto(id, codigo, nombre, telefono, email, cumpleanos));
            }
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para buscar varios contactos por su nombre"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
        return listContacts;
    }

    @Override
    public Contacto seleccionarContacto(int idContacto) {
        String sql = "SELECT con_id, con_codigo, con_nombres, con_telefono,con_avatar, con_email, con_cumpleanos "
                + "FROM contacto WHERE con_id = " + idContacto;
        Contacto contacto = null;
        Connection connection = null;

        try {
            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

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
        } catch (SQLException sqle) {
            contacto = new Contacto();
            System.err.println("Error en la consulta para buscar un contacto por Id"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
        return contacto;
    }

    @Override
    public Blob seleccionarAvatarContacto(int idContacto) {
        String sql = "SELECT con_avatar FROM contacto WHERE con_id = " + idContacto;
        Connection connection = null;
        Blob avatar = null;
        try {
            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            if (resultSet.next()) {
                avatar = resultSet.getBlob("con_avatar");
            }
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para buscar el avatar de un contacto"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
        return avatar;
    }

    @Override
    public String generarCodigoContacto() {
        String sql = "SELECT sp_genera_codigo()";
        String codigo = "";

        Connection connection = null;
        try {
            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            if (resultSet.next()) {
                codigo = resultSet.getString(1);
            }
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para buscar el avatar de un contacto"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
        return codigo;
    }

    @Override
    public void insertarContacto(Contacto contacto) {
        String sql = "INSERT INTO contacto(con_codigo, con_nombres, con_telefono,"
                + " con_avatar, con_email, con_cumpleanos, usu_id)"
                + " VALUES(?,?,?,?,?,?,?)";
        Connection connection = null;
        try {
            connection = ConnectionPool.openConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, contacto.getConCodigo());
            ps.setString(2, contacto.getConNombres());
            ps.setString(3, contacto.getConTelefono());
            ps.setBlob(4, contacto.getConAvatar());
            ps.setString(5, contacto.getConEmail());
            ps.setDate(6, Utilities.fromUtilDateToSQLDate(contacto.getConCumpleanos()));
            ps.setInt(7, contacto.getUsuId());

            ps.executeUpdate();
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para insertar un contacto"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
    }

    @Override
    public void actualizarContacto(Contacto contacto) {
        String sql = "UPDATE contacto SET "
                + "con_codigo = ?, con_nombres = ?, con_telefono = ?, "
                + "con_avatar = ?, con_email = ?, con_cumpleanos = ? "
                + "WHERE con_id = " + contacto.getConId();

        Connection connection = null;
        try {
            connection = ConnectionPool.openConnection();
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setString(1, contacto.getConCodigo());
            ps.setString(2, contacto.getConNombres());
            ps.setString(3, contacto.getConTelefono());
            ps.setBlob(4, contacto.getConAvatar());
            ps.setString(5, contacto.getConEmail());
            ps.setDate(6, Utilities.fromUtilDateToSQLDate(contacto.getConCumpleanos()));

            ps.executeUpdate();
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para actualizar un contacto"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
    }

    @Override
    public void eliminarContacto(int idContacto) {
        String sql = "DELETE FROM contacto WHERE con_id = " + idContacto;
        Connection connection = null;

        try {
            connection = ConnectionPool.openConnection();
            Statement statement = connection.createStatement();

            statement.executeUpdate(sql);
        } catch (SQLException sqle) {
            System.err.println("Error en la consulta para eliminar un contacto"
                    + "\nMensaje: " + sqle.getMessage() + "\nEstado SQL: " + sqle.getSQLState());
        } finally {
            ConnectionPool.closeConnection(connection);
        }
    }
}
