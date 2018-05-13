/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.domain;

import org.sanmarcux.util.Utilities;

import java.io.Serializable;

/**
 * @author cesardl
 */
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1615297650378310164L;

    private int usuId;
    private String usuUsuario;
    private String usuPassword;

    public int getUsuId() {
        return usuId;
    }

    public void setUsuId(int usuId) {
        this.usuId = usuId;
    }

    public String getUsuPassword() {
        return usuPassword;
    }

    public void setUsuPassword(String usuPassword) {
        this.usuPassword = Utilities.buildMySQLPassword(usuPassword);
    }

    public String getUsuUsuario() {
        return usuUsuario;
    }

    public void setUsuUsuario(String usuUsuario) {
        this.usuUsuario = usuUsuario;
    }
}
