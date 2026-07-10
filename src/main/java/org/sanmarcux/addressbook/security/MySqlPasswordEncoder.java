package org.sanmarcux.addressbook.security;

import org.sanmarcux.addressbook.util.Utilities;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * PasswordEncoder que reproduce el formato legado de MySQL {@code PASSWORD()}
 * ({@code *SHA1(SHA1(pwd))}). Permite autenticar contra los hashes ya almacenados
 * en la base de datos existente sin migrar datos.
 */
@Component
public class MySqlPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(final CharSequence rawPassword) {
        return Utilities.buildMySQLPassword(rawPassword.toString());
    }

    @Override
    public boolean matches(final CharSequence rawPassword, final String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return encode(rawPassword).equalsIgnoreCase(encodedPassword);
    }
}
