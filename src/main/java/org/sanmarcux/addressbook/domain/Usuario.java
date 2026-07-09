package org.sanmarcux.addressbook.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Usuario del sistema. El password se almacena con el formato legado de MySQL
 * ({@code *SHA1(SHA1(pwd))}) para mantener compatibilidad con la base de datos existente.
 */
public class Usuario implements Serializable {

    public enum Role {
        ADMIN, USER
    }

    private int usuId;
    private String usuUsuario;
    private String usuPassword;
    private Role role;
    private LocalDateTime lastLogin;

    public int getUsuId() {
        return usuId;
    }

    public void setUsuId(int usuId) {
        this.usuId = usuId;
    }

    public String getUsuUsuario() {
        return usuUsuario;
    }

    public void setUsuUsuario(String usuUsuario) {
        this.usuUsuario = usuUsuario;
    }

    public String getUsuPassword() {
        return usuPassword;
    }

    public void setUsuPassword(String usuPassword) {
        this.usuPassword = usuPassword;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
}
