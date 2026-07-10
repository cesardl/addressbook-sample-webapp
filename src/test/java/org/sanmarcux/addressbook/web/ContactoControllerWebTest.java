package org.sanmarcux.addressbook.web;

import org.junit.jupiter.api.Test;
import org.sanmarcux.addressbook.domain.Usuario;
import org.sanmarcux.addressbook.security.AppUserPrincipal;
import org.sanmarcux.addressbook.service.ContactoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactoController.class)
class ContactoControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactoService contactoService;

    private AppUserPrincipal principal(Usuario.Role role) {
        Usuario u = new Usuario();
        u.setUsuId(2);
        u.setUsuUsuario("cesardl");
        u.setUsuPassword("*hash");
        u.setRole(role);
        return new AppUserPrincipal(u);
    }

    @Test
    void listaRequiresAuthentication() throws Exception {
        // In the WebMvc slice an anonymous request is rejected (401); the full app
        // redirects to the form-login page (covered by the integration test).
        mockMvc.perform(get("/contactos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listaRendersForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/contactos").with(user(principal(Usuario.Role.USER))))
                .andExpect(status().isOk())
                .andExpect(view().name("contactos/lista"))
                .andExpect(model().attribute("admin", false));
    }

    @Test
    void nuevoGeneratesCode() throws Exception {
        when(contactoService.nuevoCodigo()).thenReturn("1234");
        mockMvc.perform(get("/contactos/nuevo").with(user(principal(Usuario.Role.ADMIN))))
                .andExpect(status().isOk())
                .andExpect(view().name("contactos/form"))
                .andExpect(model().attributeExists("contacto"));
    }
}
