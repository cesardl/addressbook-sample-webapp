package org.sanmarcux.addressbook.domain;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Contacto de la agenda. El avatar se maneja como {@code byte[]} (columna {@code mediumblob})
 * en lugar de {@link java.sql.Blob} para evitar problemas de ciclo de vida de la conexión.
 */
public class Contacto implements Serializable {

    private int conId;

    @Size(max = 12, message = "El código no puede exceder 12 caracteres")
    private String conCodigo = "";

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 250)
    private String conNombres = "";

    @Size(max = 9, message = "El teléfono no puede exceder 9 caracteres")
    private String conTelefono = "";

    private byte[] conAvatar;

    @Email(message = "E-mail no válido")
    @Size(max = 50)
    private String conEmail = "";

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate conCumpleanos;

    private int usuId;

    public int getConId() {
        return conId;
    }

    public void setConId(int conId) {
        this.conId = conId;
    }

    public String getConCodigo() {
        return conCodigo;
    }

    public void setConCodigo(String conCodigo) {
        this.conCodigo = conCodigo;
    }

    public String getConNombres() {
        return conNombres;
    }

    public void setConNombres(String conNombres) {
        this.conNombres = conNombres;
    }

    public String getConTelefono() {
        return conTelefono;
    }

    public void setConTelefono(String conTelefono) {
        this.conTelefono = conTelefono;
    }

    public byte[] getConAvatar() {
        return conAvatar;
    }

    public void setConAvatar(byte[] conAvatar) {
        this.conAvatar = conAvatar;
    }

    public boolean hasAvatar() {
        return conAvatar != null && conAvatar.length > 0;
    }

    public String getConEmail() {
        return conEmail;
    }

    public void setConEmail(String conEmail) {
        this.conEmail = conEmail;
    }

    public LocalDate getConCumpleanos() {
        return conCumpleanos;
    }

    public void setConCumpleanos(LocalDate conCumpleanos) {
        this.conCumpleanos = conCumpleanos;
    }

    public String getConStrcumpl() {
        return conCumpleanos == null ? "---" : conCumpleanos.toString();
    }

    public int getUsuId() {
        return usuId;
    }

    public void setUsuId(int usuId) {
        this.usuId = usuId;
    }
}
