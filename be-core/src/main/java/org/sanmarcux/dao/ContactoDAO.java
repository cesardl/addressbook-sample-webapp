package org.sanmarcux.dao;

import org.sanmarcux.domain.Contacto;

import java.sql.Blob;
import java.util.List;

public interface ContactoDAO {

    List<Contacto> listarContactos(int usuId);

    List<Contacto> listarContactos(String dato, int usuId);

    Contacto seleccionarContacto(int idContacto);

    String generarCodigoContacto();

    Blob seleccionarAvatarContacto(int idContacto);

    void insertarContacto(Contacto contacto);

    void actualizarContacto(Contacto contacto);

    void eliminarContacto(int idContacto);
}
