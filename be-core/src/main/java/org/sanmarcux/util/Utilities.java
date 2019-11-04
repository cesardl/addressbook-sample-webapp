/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Date;

/**
 * @author cesardl
 */
public class Utilities {

    /**
     * Metodo que devuelve un java.sql.Date
     * a partir de un java.util.Date
     *
     * @param date current date
     * @return date in sql instance
     */
    public static Date fromUtilDateToSQLDate(java.util.Date date) {
        if (date == null) {
            return null;
        } else {
            return new Date(date.getTime());
        }
    }

    /**
     * Metodo que devuelve un numero de tipo Int a partir de un String
     *
     * @param o the object to convert
     * @return parsed value
     */
    public static int toInteger(final Object o) {
        return Integer.parseInt(String.valueOf(o));
    }

    /**
     * Metodo que devuelve la longitud de una
     * cadena limpia
     *
     * @param cadena cadena a evaluar
     * @return el total de caracteres
     */
    public static int lengthOfString(final String cadena) {
        return cadena.trim().length();
    }

    public static String buildMySQLPassword(final String plainText) {
        byte[] utf8 = plainText.getBytes(StandardCharsets.UTF_8);
        return "*" + DigestUtils.sha1Hex(DigestUtils.sha1(utf8)).toUpperCase();
    }
}
