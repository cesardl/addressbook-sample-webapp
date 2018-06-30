package org.sanmarcux;

import org.sanmarcux.domain.Usuario;

/**
 * Created on 29/06/2018.
 *
 * @author Cesardl
 */
public class PojoFake {

    private static final Usuario adminUser;
    private static final Usuario normalUser;

    static {
        adminUser = new Usuario();
        adminUser.setUsuId(1);
        adminUser.setRole(Usuario.Role.ADMIN);

        normalUser = new Usuario();
        normalUser.setUsuId(2);
        normalUser.setUsuUsuario("cesardl");
        normalUser.setUsuPassword("123456");
        normalUser.setRole(Usuario.Role.USER);
    }

    public static Usuario getAdminUser() {
        return adminUser;
    }

    public static Usuario getNormalUser() {
        return normalUser;
    }
}
