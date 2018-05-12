package org.sanmarcux.manager;

import com.icesoft.faces.context.effects.Effect;
import com.icesoft.faces.context.effects.Highlight;
import org.sanmarcux.dao.ContactoDAO;
import org.sanmarcux.manager.base.AbstractManagerAgenda;
import org.sanmarcux.util.Utilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.component.UIParameter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

public class ManagerAgenda extends AbstractManagerAgenda {

    private static final Logger LOG = LoggerFactory.getLogger(ManagerAgenda.class);

    private Effect valueChangeEffect;
    /*Para los ventanas modales*/
    private boolean show_acerca_de = false;

    public ManagerAgenda() {
        super();
        this.valueChangeEffect = new Highlight("#fda505");
        this.valueChangeEffect.setFired(true);
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

    public void eliminarContacto(ActionEvent event) {
        LOG.debug("Entra a eliminar el contacto");
        try {
            UIParameter parameter = (UIParameter) event.getComponent().findComponent("contactId");
            int id = Utilities.toInteger(String.valueOf(parameter.getValue()));
            ContactoDAO dao = (ContactoDAO) Class.forName("org.sanmarcux.dao.impl.ContactoDAOImpl").newInstance();
            dao.eliminarContacto(id);
            valueChangeEffect.setFired(false);
        } catch (Exception e) {
            LOG.error("Error al llamar al metodo eliminarContacto {}", e.getMessage(), e);
        }
    }
}
