package org.sanmarcux.manager.base;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.sanmarcux.bd.ConnectionPool;
import org.sanmarcux.dao.ContactoDAO;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.domain.Usuario;
import org.sanmarcux.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created on 12/05/2018.
 *
 * @author Cesardl
 */
public abstract class AbstractManagerAgenda {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractManagerAgenda.class);

    private static final String NAVIGATION_TO_EDIT = "TO_EDIT";

    protected Contacto contacto;
    private List<Contacto> lista;
    private String titulo;
    private int editar;
    /*Para el autocompletar*/
    private String sugstring;
    private String sug_id;
    private Blob sug_avatar;
    /*Para el manejo de imagenes*/
    private String b_mime;

    public AbstractManagerAgenda() {
        LOG.debug("Constructor");
        this.contacto = new Contacto();
        this.editar = 0;
    }

    public String getTitulo() {
        return "<strong>" + titulo + "</strong>";
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Contacto> getLista() {
        Usuario user = getUser();
        LOG.info("Obteniendo lista de contactos del usuario {} con rol {}", user.getUsuId(), user.getRole());

        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            lista = cm.listarContactos(user.getUsuId(), user.getRole());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            LOG.error("Error al cargar la lista de contactos", ex);
            lista = new ArrayList<>();
        }

        LOG.info("Se obtuvieron {} contactos del usuario {}", lista.size(), user.getUsuUsuario());
        return lista;
    }

    public void setLista(List<Contacto> lista) {
        this.lista = lista;
    }

    public Contacto getContacto() {
        return contacto;
    }

    public void setContacto(Contacto contacto) {
        this.contacto = contacto;
    }

    public String getSugstring() {
        return sugstring;
    }

    public void setSugstring(String sugstring) {
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

    public String getB_mime() {
        return b_mime;
    }

    public void setB_mime(String b_mime) {
        this.b_mime = b_mime;
    }

    public long getTimeStamp() {
        return System.currentTimeMillis();
    }

    public String limpiarAvatar() {
        if (contacto != null) {
            contacto.setConAvatar(null);
        }
        return NAVIGATION_TO_EDIT;
    }

    public String nuevoContacto() {
        this.contacto = new Contacto();
        this.contacto.setConCodigo(obtenerCodigo());
        this.editar = 0;
        this.setTitulo("Insertar contacto");
        return NAVIGATION_TO_EDIT;
    }

    public String editarContacto() {
        this.setTitulo("Editar contacto");
        return NAVIGATION_TO_EDIT;
    }

    public String buscaContacto() {
        this.sug_avatar = null;
        this.sugstring = "";
        this.sug_id = "";
        return "TO_FIND";
    }

    private String obtenerCodigo() {
        LOG.info("Generando código aleatorio para nuevo contacto");
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            return cm.generarCodigoContacto();
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo insertarContacto {}", e.getMessage(), e);
            return "";
        }
    }

    private void insertarContacto() {
        int userId = getUser().getUsuId();
        LOG.info("Registrando nuevo contacto para el usuario {}", userId);
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            contacto.setUsuId(userId);
            cm.insertarContacto(this.contacto);
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo insertarContacto {}", e.getMessage(), e);
        }
    }

    private void actualizarContacto() {
        int userId = getUser().getUsuId();
        LOG.info("Actualizando contacto {} para el usuario {}", this.editar, userId);
        try {
            this.contacto.setConId(this.editar);
            this.contacto.setUsuId(userId);
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            cm.actualizarContacto(this.getContacto());
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo actualizarContacto {}", e.getMessage(), e);
        }
    }

    private boolean validar() {
        if (this.contacto != null) {
            if (Utilities.lengthOfString(this.contacto.getConNombres()) == 0) {
                return false;
            } else {
                return Utilities.lengthOfString(this.contacto.getConCodigo()) <= 4;
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
            return "TO_ADDRESS_BOOK";
        } else {
            return NAVIGATION_TO_EDIT;
        }
    }

    public void seleccionarContacto(ActionEvent actionEvent) {
        UIParameter component = (UIParameter) actionEvent.getComponent().findComponent("contactId");
        this.editar = Utilities.toInteger(component.getValue());

        try {
            ContactoDAO cm = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            this.setContacto(cm.seleccionarContacto(this.editar));
            if (Utilities.lengthOfString(this.getContacto().getConCodigo()) < 4) {
                this.getContacto().setConCodigo(cm.generarCodigoContacto());
            }
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo seleccionarContacto {}", e.getMessage(), e);
            this.setContacto(new Contacto());
        }
    }

    public List<Contacto> autocomplete(Object suggest) {
        if ("null".equalsIgnoreCase(suggest.toString())) {
            return Collections.emptyList();
        }

        List<Contacto> result = new ArrayList<>();

        try {
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            List<Contacto> contacts = dao.listarContactos(getUser().getUsuId(), suggest.toString());

            LOG.info("Se obtuvieron {} contacts en la búsqueda de: '{}'", contacts.size(), suggest);
            for (Contacto aLTmp : contacts) {
                Contacto c = new Contacto();
                c.setConId(aLTmp.getConId());
                c.setConCodigo(aLTmp.getConCodigo());
                c.setConNombres(aLTmp.getConNombres());
                c.setConEmail(aLTmp.getConEmail());
                c.setConStrcumpl(aLTmp.getConStrcumpl());
                c.setConAvatar(aLTmp.getConAvatar());

                result.add(c);
            }
            return result;
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo autocomplete {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public void eliminarContacto(ActionEvent event) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        LOG.debug("Entra a eliminar el contacto");
        try {
            UIParameter parameter = (UIParameter) event.getComponent().findComponent("contactId");
            int id = Utilities.toInteger(parameter.getValue());
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            dao.eliminarContacto(id);
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo eliminarContacto {}", e.getMessage(), e);
            throw e;
        }
    }

    public void loadAvatar(ActionEvent event) {
        try {
            UIParameter parameter = (UIParameter) event.getComponent().findComponent("sugContactId");
            int id = Utilities.toInteger(parameter.getValue());
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            this.setSug_avatar(dao.seleccionarAvatarContacto(id));
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException e) {
            LOG.error("Error al llamar al metodo loadAvatar {}", e.getMessage(), e);
        }
    }

    public void avatarContacto(final OutputStream stream, final Object data) throws IOException, SQLException {
        if ("vAvatar".equals(data)) {
            LOG.debug("Mostrando imágen del contacto al editar");
            if (contacto.getConAvatar() != null) {
                int length = (int) contacto.getConAvatar().length();
                stream.write(contacto.getConAvatar().getBytes(1, length));
            }
        } else if ("sugAvatar".equals(data)) {
            LOG.debug("Mostrando imágen del contacto al buscar");
            if (this.sug_avatar != null) {
                int length = (int) sug_avatar.length();
                stream.write(sug_avatar.getBytes(1, length));
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    /**
     * Metodo que devuelve el Id del usuario logeado.
     *
     * @return as user identifier
     */
    protected Usuario getUser() {
        ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
        HttpSession session = (HttpSession) context.getSession(true);
        Object attribute = session.getAttribute("usuario");

        if (attribute == null) {
            LOG.warn("No se encontró algún usuario logueado");
            return null;
        }

        LOG.info("Obteniedo identificador de usuario");
        return (Usuario) attribute;
    }

    /**
     * @param jasperPath path of the template
     * @param parameters of the template
     * @return an array of bytes with the report's data
     */
    protected byte[] getReportBytes(String jasperPath, Map<?, ?> parameters) {
        try (Connection connection = ConnectionPool.openConnection()) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            FacesContext context = FacesContext.getCurrentInstance();
            InputStream reportStream = context.getExternalContext().getResourceAsStream(jasperPath);

            JasperPrint jasperPrint = JasperFillManager.fillReport(reportStream, parameters, connection);
            JasperExportManager.exportReportToPdfStream(jasperPrint, buffer);

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
