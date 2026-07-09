package org.sanmarcux.addressbook.security;

import org.sanmarcux.addressbook.domain.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * UserDetails que además expone el id y rol del usuario para aplicar
 * la autorización por propietario en la capa de servicio.
 */
public class AppUserPrincipal implements UserDetails {

    private final int usuId;
    private final String username;
    private final String passwordHash;
    private final Usuario.Role role;

    public AppUserPrincipal(final Usuario usuario) {
        this.usuId = usuario.getUsuId();
        this.username = usuario.getUsuUsuario();
        this.passwordHash = usuario.getUsuPassword();
        this.role = usuario.getRole();
    }

    public int getUsuId() {
        return usuId;
    }

    public Usuario.Role getRole() {
        return role;
    }

    public boolean isAdmin() {
        return Usuario.Role.ADMIN.equals(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
