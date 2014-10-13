package org.sanmarcux.manager;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.dao.ContactoDAO;
import org.sanmarcux.util.Utilities;

public class ManagerAgenda implements Serializable {

    private List<Contacto> lista;
    private Contacto contacto;
    private int b_id;
    private String b_nombre;
    private String b_email;
    private String titulo;
    private int editar;
    private Effect valueChangeEffect;
    /*Para el autocompletar*/
    private String sugstring;
    private String sug_id;
    private Blob sug_avatar;
    /*Para el manejo de imagenes*/
    private String b_file_name;
    private String b_mime;
    private Utilities util;
    /*Para los ventanas modales*/
    private boolean show_acerca_de = false;

    public ManagerAgenda() {
        System.out.println("constructor de ManagerAgenda");
        this.contacto = new Contacto();
        util = new Utilities();
        this.editar = 0;
        this.valueChangeEffect = new Highlight("#fda505");
        this.valueChangeEffect.setFired(true);
    }

    public String getTitulo() {
        return "<strong>" + titulo + "</strong>";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Contacto> getLista() {
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            lista = new ArrayList<Contacto>(cm.listarContactos(util.getUsuId()));
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

    public boolean isShow_acerca_de() {
        return show_acerca_de;
    }

    public Effect getValueChangeEffect() {
        return valueChangeEffect;
    }

    public void setValueChangeEffect(Effect valueChangeEffect) {
        this.valueChangeEffect = valueChangeEffect;
    }

    public void effectChangeListener(ValueChangeEvent event) {
        valueChangeEffect.setFired(false);
    }

    public String getMensajeInfo() {
        return "<h3>Este es un pequeño ejemplo de algunos componentes de <i>IceFaces</i>, <br/>"
                + "consultas a base de datos <i>MySQL</i> y <br/>"
                + "generaci&oacute;n dereportes con <i>JasperReports</i></h3>";
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
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

    public void showModalAcercaDe(ActionEvent event) {
        this.show_acerca_de = !this.show_acerca_de;
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
        System.out.println("entra a eliminar el contacto");
        try {
            UIParameter parameter = (UIParameter) event.getComponent().findComponent("contactId");
            int id = Utilities.toInteger(String.valueOf(parameter.getValue()));
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            dao.eliminarContacto(id);
            valueChangeEffect.setFired(false);
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
}
