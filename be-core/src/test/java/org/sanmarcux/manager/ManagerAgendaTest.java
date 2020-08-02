package org.sanmarcux.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sanmarcux.PojoFake;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.manager.base.AbstractManagerAgenda;

import javax.faces.component.UIComponent;
import javax.faces.component.UIParameter;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created on 29/06/2018.
 *
 * @author Cesardl
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "javax.faces.context.*")
public class ManagerAgendaTest {

    private static class ManagerAgendaImpl extends AbstractManagerAgenda {
    }

    private final AbstractManagerAgenda addressBook = new ManagerAgendaImpl();

    @Test
    public void preparingForInsertContactTest() {
        String result = addressBook.nuevoContacto();

        assertNotNull(addressBook.getContacto().getConCodigo());
        assertTrue(addressBook.getContacto().getConNombres().isEmpty());
        assertEquals("<strong>Insertar contacto</strong>", addressBook.getTitulo());
        assertEquals("TO_EDIT", result);
    }

    @Test
    public void preparingForEditContactTest() {
        // Invoking action
        String result = addressBook.editarContacto();

        assertNotNull(addressBook.getContacto().getConCodigo());
        assertEquals("<strong>Editar contacto</strong>", addressBook.getTitulo());
        assertEquals("TO_EDIT", result);

        // Invoking actionListener
        ActionEvent actionEvent = mock(ActionEvent.class);
        UIComponent uiComponent = mock(UIComponent.class);
        UIParameter uiParameter = mock(UIParameter.class);

        when(actionEvent.getComponent()).thenReturn(uiComponent);
        when(uiComponent.findComponent("contactId")).thenReturn(uiParameter);
        when(uiParameter.getValue()).thenReturn(11);

        addressBook.seleccionarContacto(actionEvent);

        assertFalse(addressBook.getContacto().getConNombres().isEmpty());
        assertNotNull(addressBook.getContacto().getConTelefono());
        assertNotNull(addressBook.getContacto().getConAvatar());
        assertNotNull(addressBook.getContacto().getConEmail());
        assertNotNull(addressBook.getContacto().getConCumpleanos());
        assertNotNull(addressBook.getContacto().getConStrcumpl());
    }

    private HttpSession mockSession() {
        mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(mock(FacesContext.class));
        when(FacesContext.getCurrentInstance().getExternalContext()).thenReturn(mock(ExternalContext.class));

        HttpSession httpSession = mock(HttpSession.class);
        when(FacesContext.getCurrentInstance().getExternalContext().getSession(true)).thenReturn(httpSession);
        return httpSession;
    }

    @Test
    public void getContactsIfAnAdmin() {
        HttpSession httpSession = mockSession();
        when(httpSession.getAttribute("usuario")).thenReturn(PojoFake.getAdminUser());

        List<Contacto> result = addressBook.getLista();
        assertFalse(result.isEmpty());
        assertEquals(30, result.size());
    }

    @Test
    public void getContactsIfAnUser() {
        HttpSession httpSession = mockSession();
        when(httpSession.getAttribute("usuario")).thenReturn(PojoFake.getNormalUser());

        List<Contacto> result = addressBook.getLista();
        assertFalse(result.isEmpty());
        assertEquals(13, result.size());
    }
}
