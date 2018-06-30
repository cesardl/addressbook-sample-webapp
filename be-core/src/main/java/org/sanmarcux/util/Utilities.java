/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.sql.Date;

/**
 * @author cesardl
 */
public class Utilities {

    private static final Logger LOG = LoggerFactory.getLogger(Utilities.class);

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
        try {
            byte[] utf8 = plainText.getBytes("UTF-8");
            return "*" + DigestUtils.sha1Hex(DigestUtils.sha1(utf8)).toUpperCase();
        } catch (UnsupportedEncodingException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
