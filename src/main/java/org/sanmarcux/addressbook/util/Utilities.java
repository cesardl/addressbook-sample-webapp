package org.sanmarcux.addressbook.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * Utilidades varias. Conservado de la versión JSF original.
 */
public final class Utilities {

    private Utilities() {
    }

    /**
     * Reproduce el hashing legado de la función {@code PASSWORD()} de MySQL:
     * {@code * + SHA1(SHA1(utf8(pwd)))} en mayúsculas.
     * <p>
     * Es compatibilidad-crítica: los passwords en la base de datos existente
     * están almacenados con este formato, por lo que el login sigue funcionando
     * sin necesidad de migrar datos.
     *
     * @param plainText password en texto plano
     * @return hash con el formato {@code *ABCD...}
     */
    public static String buildMySQLPassword(final String plainText) {
        byte[] utf8 = plainText.getBytes(StandardCharsets.UTF_8);
        return "*" + DigestUtils.sha1Hex(DigestUtils.sha1(utf8)).toUpperCase();
    }
}
