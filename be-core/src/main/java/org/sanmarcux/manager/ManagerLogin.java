/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.manager;

import org.sanmarcux.dao.UsuarioDAO;
import org.sanmarcux.domain.Usuario;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

/**
 * @author cesardl
 */
public class ManagerLogin {

    private static final String NAVIGATION_TO_LOGIN = "TO_LOGIN";

    private Usuario usuario;
    private boolean error;

    /**
     * Creates a new instance of ManagerUsuario
     */
    public ManagerLogin() {
        this.usuario = new Usuario();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String validarUsuario() {
        try {
            UsuarioDAO dao = (UsuarioDAO) Class.forName("org.sanmarcux.dao.impl.UsuarioDAOImpl").newInstance();
            Usuario user = dao.getUsuario(this.usuario);

            if (user == null) {
                this.setError(true);
                return NAVIGATION_TO_LOGIN;
            } else {
                dao.actualizarUltimoAcceso(user.getUsuId());

                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                HttpSession session = (HttpSession) context.getSession(false);
                session.setAttribute("usuario", user);
                return "TO_ADDRESS_BOOK";
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            this.setError(true);
            return NAVIGATION_TO_LOGIN;
        }
    }

    public String cerrarSesion() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);

        Enumeration attributes = session.getAttributeNames();
        while (attributes.hasMoreElements()) {
            session.removeAttribute((String) attributes.nextElement());
        }
        session.invalidate();

        return NAVIGATION_TO_LOGIN;
    }
}
