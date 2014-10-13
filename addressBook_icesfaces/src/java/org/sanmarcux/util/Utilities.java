/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import org.sanmarcux.domain.Usuario;

/**
 *
 * @author cesardl
 */
public class Utilities {

    public static final int ERROR = -1;

    /**
     * Metodo que devuelve un java.sql.Date
     * a partir de un java.util.Date
     * @param date
     * @return
     */
    public static Date fromUtilDateToSQLDate(java.util.Date date) {
        if (date instanceof java.util.Date) {
            return new Date(date.getTime());
        } else {
            return null;
        }
    }

    /**
     * Metodo que devuelve un arreglo de bytes
     * a partir de una java.io.File
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] fromFiletoByteArray(File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);

        byte fileContent[] = new byte[(int) file.length()];
        int read = fin.read(fileContent);
        fin.close();
        if (read == -1) {
            return null;
        } else {
            return fileContent;
        }
    }

    /**
     * Metodo que devuelve un numero de tipo Int
     * a partir de un String
     * @param cadena
     * @return
     */
    public static int toInteger(String cadena) {
        try {
            return Integer.parseInt(cadena);
        } catch (NumberFormatException nfe) {
            return ERROR;
        }
    }

    /**
     * Metodo que devuelve la longitud de una
     * cadena limpia
     * @param cadena
     * @return
     */
    public static int lengthOfString(String cadena) {
        return cadena.trim().length();
    }

    /**
     * 
     * @return
     */
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    /**
     * Metodo que devuelve el Id del usuario logeado
     * @return
     */
    public int getUsuId() {
        try {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            HttpSession session = (HttpSession) context.getSession(true);
            return ((Usuario) session.getAttribute("usuario")).getUsuId();
        } catch (Exception e) {
            return 0;
        }
    }
}
