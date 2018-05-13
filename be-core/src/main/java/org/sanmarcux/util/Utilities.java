/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanmarcux.bd.ConnectionPool;
import org.sanmarcux.domain.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

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
     * Metodo que devuelve un arreglo de bytes
     * a partir de una java.io.File
     *
     * @param file the desired file
     * @return an array of bytes
     * @throws IOException if can not read file
     */
    public static byte[] fromFileToByteArray(final File file) throws IOException {
        FileInputStream fin = new FileInputStream(file);

        byte fileContent[] = new byte[(int) file.length()];
        int read = fin.read(fileContent);
        fin.close();
        if (read == -1) {
            throw new IOException();
        } else {
            return fileContent;
        }
    }

    /**
     * Metodo que devuelve un numero de tipo Int a partir de un String
     *
     * @param o the object to convert
     * @return parsed value
     */
    public static int toInteger(Object o) {
        return Integer.parseInt(String.valueOf(o));
    }

    /**
     * Metodo que devuelve la longitud de una
     * cadena limpia
     *
     * @param cadena
     * @return
     */
    public static int lengthOfString(final String cadena) {
        return cadena.trim().length();
    }

    /**
     * @return
     */
    public static FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    /**
     * Metodo que devuelve el Id del usuario logeado
     *
     * @return as user identifier
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

    /**
     * @param jasperPath path of the template
     * @param parameters of the template
     * @return an array of bytes with the report's data
     */
    public byte[] getReportBytes(String jasperPath, Map<?, ?> parameters) {
        try {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            FacesContext context = FacesContext.getCurrentInstance();
            InputStream reportStream = context.getExternalContext().getResourceAsStream(jasperPath);

            Connection connection = ConnectionPool.openConnection();
            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
            JasperExportManager.exportReportToPdfStream(jasperPrint, buffer);

            ConnectionPool.closeQuietly(connection);

            byte[] bytes = buffer.toByteArray();
            buffer.flush();
            buffer.close();
            return bytes;
        } catch (JRException | IOException | SQLException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
}
