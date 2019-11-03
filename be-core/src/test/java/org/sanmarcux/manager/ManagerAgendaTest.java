package org.sanmarcux.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sanmarcux.PojoFake;
import org.sanmarcux.domain.Contacto;
import org.sanmarcux.manager.base.AbstractManagerAgenda;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created on 29/06/2018.
 *
 * @author Cesardl
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "javax.faces.context.*")
public class ManagerAgendaTest {

    private class ManagerAgendaImpl extends AbstractManagerAgenda {
    }

    private AbstractManagerAgenda addressBook = new ManagerAgendaImpl();

    @Test
    public void getTitleTest() {
        String title = "some title";

        addressBook.setTitulo(title);

        String result = addressBook.getTitulo();

        assertEquals("<strong>some title</strong>", result);
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
