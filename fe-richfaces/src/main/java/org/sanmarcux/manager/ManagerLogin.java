/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.manager;

import java.util.Enumeration;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.sanmarcux.dao.UsuarioDAO;
import org.sanmarcux.domain.Usuario;

/**
 *
 * @author cesardl
 */
public class ManagerLogin {

    private Usuario usuario;
    private boolean error;

    /** Creates a new instance of ManagerUsuario */
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
                return "TO_LOGIN";
            } else {
                ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
                HttpSession session = (HttpSession) context.getSession(false);
                session.setAttribute("usuario", user);
                return "TO_ADDRESSBOOK";
            }
        } catch (Exception e) {
            this.setError(true);
            return "TO_LOGIN";
        }
    }

    public String cerrarSesion() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);

        Enumeration atributos = session.getAttributeNames();
        while (atributos.hasMoreElements()) {
            session.removeAttribute((String) atributos.nextElement());
        }
        session.invalidate();

        return "TO_LOGIN";
    }
}
