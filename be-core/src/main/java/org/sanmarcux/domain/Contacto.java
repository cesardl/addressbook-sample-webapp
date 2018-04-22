/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sanmarcux.domain;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

/**
 * 
 * @author cesardl
 */
public class Contacto implements Serializable {

    private static final long serialVersionUID = 1L;
    private int conId;
    private String conCodigo;
    private String conNombres;
    private String conTelefono;
    private Blob conAvatar;
    private String conEmail;
    private Date conCumpleanos;
    private String conStrcumpl;
    private int usuId;

    public Contacto() {
        this.conCodigo = "";
        this.conNombres = "";
        this.conTelefono = "";
        this.conAvatar = null;
        this.conEmail = "";
        this.conCumpleanos = null;
        this.conStrcumpl = "";
    }

    public Contacto(int conId, String conCodigo, String conNombres,
            String conTelefono, String conEmail, Date conCumpleanos) {
        this.conId = conId;
        this.conCodigo = conCodigo;
        this.conNombres = conNombres;
        this.conTelefono = conTelefono;
        this.conEmail = conEmail;
        this.conCumpleanos = conCumpleanos;
        if (conCumpleanos == null) {
            this.setConStrcumpl("---");
        } else {
            this.setConStrcumpl(String.valueOf(this.conCumpleanos));
        }
    }

    public Contacto(int conId, String conCodigo, String conNombres,
            String conTelefono, Blob conAvatar, String conEmail, Date conCumpleanos) {
        this.conId = conId;
        this.conCodigo = conCodigo;
        this.conNombres = conNombres;
        this.conTelefono = conTelefono;
        this.conAvatar = conAvatar;
        this.conEmail = conEmail;
        this.conCumpleanos = conCumpleanos;
        if (conCumpleanos == null) {
            this.setConStrcumpl("---");
        } else {
            this.setConStrcumpl(String.valueOf(this.conCumpleanos));
        }
    }

    public String getConCodigo() {
        return conCodigo;
    }

    public void setConCodigo(String conCodigo) {
        this.conCodigo = conCodigo;
    }

    public Date getConCumpleanos() {
        return conCumpleanos;
    }

    public void setConCumpleanos(Date conCumpleanos) {
        this.conCumpleanos = conCumpleanos;
    }

    public Blob getConAvatar() {
        return conAvatar;
    }

    public void setConAvatar(Blob conAvatar) {
        this.conAvatar = conAvatar;
    }

    public String getConEmail() {
        return conEmail;
    }

    public void setConEmail(String conEmail) {
        this.conEmail = conEmail;
    }

    public int getConId() {
        return conId;
    }

    public void setConId(int conId) {
        this.conId = conId;
    }

    public String getConNombres() {
        return conNombres;
    }

    public void setConNombres(String conNombres) {
        this.conNombres = conNombres;
    }

    public String getConStrcumpl() {
        return conStrcumpl;
    }

    public void setConStrcumpl(String conStrcumpl) {
        this.conStrcumpl = conStrcumpl;
    }

    public String getConTelefono() {
        return conTelefono;
    }

    public void setConTelefono(String conTelefono) {
        this.conTelefono = conTelefono;
    }

    public int getUsuId() {
        return usuId;
    }

    public void setUsuId(int usuId) {
        this.usuId = usuId;
    }
}
