package org.sanmarcux.manager;

import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;
import org.sanmarcux.manager.base.AbstractManagerAgenda;
import org.sanmarcux.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;
import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ManagerAgenda extends AbstractManagerAgenda {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerAgenda.class);

    public ManagerAgenda() {
        super();
    }

    public void cargarImagen(UploadEvent event) {
        try {
            UploadItem item = event.getUploadItem();

            this.setB_mime(item.getContentType());
            this.setB_file_name(item.getFileName());
            File file = item.getFile();
            contacto.setConAvatar(new SerialBlob(Utilities.fromFileToByteArray(file)));
            boolean deleted = file.delete();

            if (deleted) {
                LOG.debug("Imagen temporal fue borrada {}", file.getAbsoluteFile());
            } else {
                LOG.warn("La imagen no pudo ser borrada");
            }
        } catch (IOException | SQLException e) {
            LOG.error("error al cargar la imagen", e);
        }
    }

    public void verContacto(ActionEvent event) throws IOException {
        UIParameter parameter = (UIParameter) event.getComponent().findComponent("contactId");
        int contactId = Utilities.toInteger(parameter.getValue());

        String jasperPath = "/reportes/report_contact_by_id.jasper";

        Map<String, Integer> parameters = new HashMap<>();
        parameters.put("CON_ID", contactId);

        InputStream input = new ByteArrayInputStream(util.getReportBytes(jasperPath, parameters));

        int size = input.available();
        byte[] pdf = new byte[size];
        int read = input.read(pdf);

        LOG.debug("Se leyeron {} bytes para la generación del reporte PDF por contacto", read);

        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        response.setContentType("application/pdf");
        response.setContentLength(size);
        response.setHeader("Content-Disposition", "inline;filename=reporte.pdf");
        ServletOutputStream output = response.getOutputStream();
        output.write(pdf);
        output.flush();
        output.close();
        FacesContext.getCurrentInstance().responseComplete();
    }

    public void reporteContactos(OutputStream out, Object data) throws IOException {
        if (data != null) {
            if (data.equals("allContacts")) {
                String jasper_path = "/reportes/report_all_contacts.jasper";

                Map<String, Integer> parameters = new HashMap<>();
                parameters.put("USU_ID", util.getUsuId());

                InputStream input = new ByteArrayInputStream(util.getReportBytes(jasper_path, parameters));

                int size = input.available();
                byte[] pdf = new byte[size];

                int read = input.read(pdf);

                LOG.debug("Se leyeron {} bytes para la generación del reporte PDF de contactos", read);

                out.write(pdf);

                input.close();
                out.flush();
                out.close();
            }
        }
    }
}
