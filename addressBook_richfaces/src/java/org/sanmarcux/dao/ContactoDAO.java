package org.sanmarcux.dao;

import java.sql.Blob;
import java.util.List;
import org.sanmarcux.domain.Contacto;

public interface ContactoDAO {

    public List<Contacto> listarContactos(int usuId);

    public List<Contacto> listarContactos(String dato, int usuId);

    public Contacto seleccionarContacto(int idContacto);

    public String generarCodigoContacto();

    public Blob seleccionarAvatarContacto(int idContacto);

    public void insertarContacto(Contacto contacto);

    public void actualizarContacto(Contacto contacto);

    public void eliminarContacto(int idContacto);
}
