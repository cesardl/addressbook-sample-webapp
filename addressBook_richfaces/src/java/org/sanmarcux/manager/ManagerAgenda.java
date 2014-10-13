package org.sanmarcux.manager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import net.sf.jasperreports.engine.JRException;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.dao.ContactoDAO;
import org.sanmarcux.util.Utilities;

public class ManagerAgenda {

    private List<Contacto> lista;
    private Contacto contacto;
    private int b_id;
    private String b_nombre;
    private String b_email;
    private String titulo;
    private int editar;
    /*Para el autocompletar*/
    private String sugstring;
    private String sug_id;
    private Blob sug_avatar;
    /*Para el manejo de imagenes*/
    private String b_file_name;
    private String b_mime;
    private Utilities util;

    public ManagerAgenda() {
        this.contacto = new Contacto();
        util = new Utilities();
        this.editar = 0;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Contacto> getLista() {
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            lista = cm.listarContactos(util.getUsuId());
        } catch (Exception ex) {
            System.err.println("Error al cargar la lista de contactos " + ex.getMessage());
            lista = new ArrayList<Contacto>();
        }
        return lista;
    }

    public void setLista(ArrayList<Contacto> lista) {
        this.lista = lista;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public String getB_email() {
        return b_email;
    }

    public void setB_email(String b_email) {
        this.b_email = b_email;
    }

    public int getB_id() {
        return b_id;
    }

    public void setB_id(int b_id) {
        this.b_id = b_id;
    }

    public String getB_nombre() {
        return b_nombre;
    }

    public void setB_nombre(String b_nombre) {
        this.b_nombre = b_nombre;
    }

    public String getSugstring() {
        return sugstring;
    }

    public void setSugstring(String sugstring) {
        if (sugstring != null) {
            sugstring = "";
        }
        this.sugstring = sugstring;
    }

    public String getSug_id() {
        return sug_id;
    }

    public void setSug_id(String sug_id) {
        this.sug_id = sug_id;
    }

    public Blob getSug_avatar() {
        return sug_avatar;
    }

    public void setSug_avatar(Blob sug_avatar) {
        this.sug_avatar = sug_avatar;
    }

    public String getB_file_name() {
        return b_file_name;
    }

    public void setB_file_name(String b_file_name) {
        this.b_file_name = b_file_name;
    }

    public String getB_mime() {
        return b_mime;
    }

    public void setB_mime(String b_mime) {
        this.b_mime = b_mime;
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public void cargarImagen(UploadEvent event) {
        try {
            UploadItem item = event.getUploadItem();

            this.setB_mime(item.getContentType());
            this.setB_file_name(item.getFileName());
            File file = item.getFile();
            contacto.setConAvatar(new SerialBlob(Utilities.fromFiletoByteArray(file)));
            file.delete();
        } catch (Exception e) {
            System.err.println("error al cargar la imagen: " + e.toString());
        }
    }

    public String limpiarAvatar() {
        if (contacto != null) {
            contacto.setConAvatar(null);
        }
        return "TO_EDIT";
    }

    public String nuevoContacto() {
        this.contacto = new Contacto();
        this.contacto.setConCodigo(obtenerCodigo());
        this.editar = 0;
        this.setTitulo("Insertar contacto");
        return "TO_EDIT";
    }

    public String editarContacto() {
        this.setTitulo("Editar contacto");
        return "TO_EDIT";
    }

    public String buscaContacto() {
        this.sug_avatar = null;
        this.sugstring = "";
        this.sug_id = "";
        return "TO_FIND";
    }

    private String obtenerCodigo() {
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            return cm.generarCodigoContacto();
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo insertarContacto " + e.getMessage());
            return "";
        }
    }

    public void insertarContacto() {
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            contacto.setUsuId(util.getUsuId());
            cm.insertarContacto(this.contacto);
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo insertarContacto " + e.getMessage());
        }
    }

    public void actualizarContacto() {
        try {
            this.contacto.setConId(this.editar);
            this.contacto.setUsuId(util.getUsuId());
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            cm.actualizarContacto(this.getContacto());
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo actualizarContacto " + e.getMessage());
        }
    }

    private boolean validar() {
        if (this.contacto != null) {
            if (Utilities.lengthOfString(this.contacto.getConNombres()) == 0) {
                return false;
            } else if (Utilities.lengthOfString(this.contacto.getConCodigo()) > 4) {
                return false;
            }
        }
        return true;
    }

    public String manipularContacto() {
        if (this.validar()) {
            if (this.editar == 0) {
                insertarContacto();
            } else {
                actualizarContacto();
            }
            return "TO_ADDRESSBOOK";
        } else {
            return "TO_EDIT";
        }
    }

    public void seleccionarContacto(ActionEvent actionEvent) {
        UIParameter component = (UIParameter) actionEvent.getComponent().findComponent("contactId");
        this.editar = Utilities.toInteger(String.valueOf(component.getValue()));

        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            this.setContacto(cm.seleccionarContacto(this.editar));
            if (Utilities.lengthOfString(this.getContacto().getConCodigo()) < 4) {
                this.getContacto().setConCodigo(cm.generarCodigoContacto());
            }
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo seleccionarContacto " + e.getMessage());
            this.setContacto(new Contacto());
        }
    }

    public List<Contacto> autocomplete(Object suggest) {
        String dato = String.valueOf(suggest);
        List<Contacto> result = new ArrayList<Contacto>();
        try {
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            List<Contacto> lTmp = dao.listarContactos(dato, util.getUsuId());
            for (int i = 0; i < lTmp.size(); i++) {
                Contacto c = new Contacto();
                c.setConId(lTmp.get(i).getConId());
                c.setConCodigo(lTmp.get(i).getConCodigo());
                c.setConNombres(lTmp.get(i).getConNombres());
                c.setConEmail(lTmp.get(i).getConEmail());
                c.setConStrcumpl(lTmp.get(i).getConStrcumpl());
                c.setConAvatar(lTmp.get(i).getConAvatar());

                result.add(c);
            }
            return result;
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo autocomplete " + e.getMessage());
            return null;
        }
    }

    public void loadAvatar(ActionEvent event) {
        try {
            UIParameter parameter = (UIParameter) event.getComponent().findComponent("sugContactId");
            int id = Utilities.toInteger(String.valueOf(parameter.getValue()));
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            this.setSug_avatar(dao.seleccionarAvatarContacto(id));
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo loadAvatar " + e.getMessage());
        }
    }

    public void eliminarContacto(ActionEvent event) {
        try {
            UIParameter parameter = (UIParameter) event.getComponent().findComponent("contactId");
            int id = Utilities.toInteger(String.valueOf(parameter.getValue()));
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            dao.eliminarContacto(id);
        } catch (Exception e) {
            System.err.println("Error al llamar al metodo eliminarContacto " + e.getMessage());
        }
    }

    public void avatarContacto(OutputStream stream, Object data) throws IOException, SQLException {
        if (data.equals("vAvatar")) {
            if (contacto.getConAvatar() != null) {
                int length = (int) contacto.getConAvatar().length();
                stream.write(contacto.getConAvatar().getBytes(1, length));
            }
        } else if (data.equals("sugAvatar")) {
            if (this.sug_avatar != null) {
                int length = (int) sug_avatar.length();
                stream.write(sug_avatar.getBytes(1, length));
            }
        }
    }

    public void verContacto(ActionEvent event) throws IOException, JRException {
        UIParameter parameter = (UIParameter) event.getComponent().findComponent("contactId");
        int contactId = Utilities.toInteger(String.valueOf(parameter.getValue()));

        String jasper_path = "/reportes/report_contact_by_id.jasper";

        Map parametros = new HashMap();
        parametros.put("CON_ID", contactId);

        InputStream input = new ByteArrayInputStream(util.getReportBytes(jasper_path, parametros));

        int size = input.available();
        byte[] pdf = new byte[size];
        input.read(pdf);

        HttpServletResponse response = (HttpServletResponse) Utilities.getFacesContext().getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(size);
        response.setHeader("Content-Disposition", "inline;filename=reporte.pdf");
        ServletOutputStream output = response.getOutputStream();
        output.write(pdf);
        output.flush();
        output.close();
        Utilities.getFacesContext().responseComplete();
    }

    public void reporteContactos(OutputStream out, Object data) throws IOException, JRException, SQLException {
        if (data != null) {
            if (data.equals("allContacts")) {
                String jasper_path = "/reportes/report_all_contacts.jasper";

                Map parametros = new HashMap();
                parametros.put("USU_ID", util.getUsuId());

                InputStream input = new ByteArrayInputStream(util.getReportBytes(jasper_path, parametros));

                int size = input.available();
                byte[] pdf = new byte[size];

                input.read(pdf);
                out.write(pdf);

                input.close();
                out.flush();
                out.close();
            }
        }
    }
}
