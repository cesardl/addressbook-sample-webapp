package org.sanmarcux.addressbook.security;

import org.sanmarcux.addressbook.repository.UsuarioRepository;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Actualiza {@code last_login} tras un inicio de sesión exitoso
 * (equivalente a {@code ManagerLogin.actualizarUltimoAcceso} de la versión JSF).
 */
@Component
public class LoginSuccessListener {

    private final UsuarioRepository usuarioRepository;

    public LoginSuccessListener(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @EventListener
    public void onSuccess(final AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if (principal instanceof AppUserPrincipal user) {
            usuarioRepository.actualizarUltimoAcceso(user.getUsuId());
        }
    }
}
