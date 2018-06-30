package org.sanmarcux.dao;

import org.sanmarcux.domain.Contacto;
import org.sanmarcux.domain.Usuario;

import java.sql.Blob;
import java.util.List;

public interface ContactoDAO {

    List<Contacto> listarContactos(int usuId, Usuario.Role role);

    List<Contacto> listarContactos(int usuId, String dato);

    Contacto seleccionarContacto(int idContacto);

    String generarCodigoContacto();

    Blob seleccionarAvatarContacto(int idContacto);

    void insertarContacto(Contacto contacto);

    void actualizarContacto(Contacto contacto);

    void eliminarContacto(int idContacto);
}
