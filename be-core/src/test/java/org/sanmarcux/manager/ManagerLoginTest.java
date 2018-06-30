package org.sanmarcux.manager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sanmarcux.PojoFake;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.*;

/**
 * Created on 29/06/2018.
 *
 * @author Cesardl
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "javax.faces.context.*")
public class ManagerLoginTest {

    private ManagerLogin manager = new ManagerLogin();

    @Test
    public void successLoginTest() {
        manager.setUsuario(PojoFake.getNormalUser());

        mockStatic(FacesContext.class);
        when(FacesContext.getCurrentInstance()).thenReturn(mock(FacesContext.class));
        when(FacesContext.getCurrentInstance().getExternalContext()).thenReturn(mock(ExternalContext.class));
        when(FacesContext.getCurrentInstance().getExternalContext().getSession(false)).thenReturn(mock(HttpSession.class));

        String result = manager.validarUsuario();
        assertNotNull(result);
        assertEquals("TO_ADDRESS_BOOK", result);
        assertFalse(manager.isError());
    }

    @Test
    public void failedLoginTest() {
        String result = manager.validarUsuario();
        assertNotNull(result);
        assertEquals("TO_LOGIN", result);
        assertTrue(manager.isError());
    }
}
