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
    private static final String CONTACT_DAO_CLASS_NAME = "org.sanmarcux.dao.impl.ContactoDAOImpl";

    protected Contacto contacto;
    /*Para el manejo de imagenes*/
    protected String avatarMimeType;

    private List<Contacto> lista;
    private String titulo;
    private int editar;

    /*Para el autocompletar*/
    private String suggestedString;
    private String suggestedContactId;
    private Blob suggestedContactAvatar;

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
            ContactoDAO cm = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
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

    public String getSuggestedString() {
        return suggestedString;
    }

    public void setSuggestedString(String suggestedString) {
        this.suggestedString = suggestedString;
    }

    public String getSuggestedContactId() {
        return suggestedContactId;
    }

    public void setSuggestedContactId(String suggestedContactId) {
        this.suggestedContactId = suggestedContactId;
    }

    public Blob getSuggestedContactAvatar() {
        return suggestedContactAvatar;
    }

    public void setSuggestedContactAvatar(Blob suggestedContactAvatar) {
        this.suggestedContactAvatar = suggestedContactAvatar;
    }

    public String getAvatarMimeType() {
        return avatarMimeType;
    }

    public void setAvatarMimeType(String avatarMimeType) {
        this.avatarMimeType = avatarMimeType;
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
        this.suggestedContactId = "";
        this.suggestedContactAvatar = null;
        this.suggestedString = "";
        return "TO_FIND";
    }

    private String obtenerCodigo() {
        LOG.info("Generando código aleatorio para nuevo contacto");
        try {
            ContactoDAO cm = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
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
            ContactoDAO cm = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
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
            ContactoDAO cm = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
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
            ContactoDAO cm = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
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
            ContactoDAO dao = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
            List<Contacto> contacts = dao.listarContactos(getUser().getUsuId(), getUser().getRole(), suggest.toString());

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
            ContactoDAO dao = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
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
            ContactoDAO dao = (ContactoDAO) Class.forName(CONTACT_DAO_CLASS_NAME).newInstance();
            this.setSuggestedContactAvatar(dao.seleccionarAvatarContacto(id));
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
            if (this.suggestedContactAvatar != null) {
                int length = (int) suggestedContactAvatar.length();
                stream.write(suggestedContactAvatar.getBytes(1, length));
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
