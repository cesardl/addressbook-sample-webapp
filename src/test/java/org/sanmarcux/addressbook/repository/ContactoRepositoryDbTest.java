package org.sanmarcux.addressbook.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sanmarcux.addressbook.domain.Contacto;
import org.sanmarcux.addressbook.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba de integración de la capa de persistencia contra MySQL 8 real (Testcontainers).
 * Se omite automáticamente si Docker no está disponible.
 */
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class ContactoRepositoryDbTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("address_book");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
    }

    @Autowired
    private ContactoRepository repository;

    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("DROP TABLE IF EXISTS contacto");
        jdbc.execute("DROP TABLE IF EXISTS usuario");
        jdbc.execute("""
                CREATE TABLE usuario (
                  usu_id INT AUTO_INCREMENT PRIMARY KEY,
                  usu_usuario VARCHAR(41) NOT NULL UNIQUE,
                  usu_password VARCHAR(41) NOT NULL,
                  usu_role ENUM('USER','ADMIN') DEFAULT 'USER',
                  last_login TIMESTAMP NULL
                )""");
        jdbc.execute("""
                CREATE TABLE contacto (
                  con_id INT AUTO_INCREMENT PRIMARY KEY,
                  con_codigo VARCHAR(12) NOT NULL,
                  con_nombres VARCHAR(250) NOT NULL,
                  con_telefono VARCHAR(9),
                  con_avatar MEDIUMBLOB,
                  con_email VARCHAR(50),
                  con_cumpleanos DATE,
                  usu_id INT NOT NULL
                )""");
        jdbc.update("INSERT INTO usuario(usu_id, usu_usuario, usu_password, usu_role) VALUES (1,'admin','*x','ADMIN')");
        jdbc.update("INSERT INTO usuario(usu_id, usu_usuario, usu_password, usu_role) VALUES (2,'cesardl','*y','USER')");
        // 3 contactos del admin, 2 del usuario
        for (int i = 0; i < 3; i++) {
            jdbc.update("INSERT INTO contacto(con_codigo, con_nombres, usu_id) VALUES (?,?,1)",
                    "A" + i, "Admin Contact " + i);
        }
        jdbc.update("INSERT INTO contacto(con_codigo, con_nombres, con_email, usu_id) VALUES ('U1','Ana Lopez','ana@x.com',2)");
        jdbc.update("INSERT INTO contacto(con_codigo, con_nombres, usu_id) VALUES ('U2','Beto Ruiz',2)");
    }

    @Test
    void adminSeesAllUserSeesOwn() {
        assertEquals(5, repository.countVisible(1, Usuario.Role.ADMIN));
        assertEquals(2, repository.countVisible(2, Usuario.Role.USER));
    }

    @Test
    void searchFiltersByName() {
        assertEquals(1, repository.countFiltered(2, Usuario.Role.USER, "Ana"));
        List<Contacto> page = repository.findPage(2, Usuario.Role.USER, "Ana", 0, 10, "con_nombres", true);
        assertEquals(1, page.size());
        assertEquals("Ana Lopez", page.get(0).getConNombres());
    }

    @Test
    void userCannotSeeOthersViaSearch() {
        // "Admin" contacts belong to user 1; user 2 must not find them.
        assertEquals(0, repository.countFiltered(2, Usuario.Role.USER, "Admin"));
    }

    @Test
    void generarCodigoIsUnique() {
        String code = repository.generarCodigo();
        assertNotNull(code);
        assertEquals(0, (int) jdbc.queryForObject(
                "SELECT COUNT(*) FROM contacto WHERE con_codigo = ?", Integer.class, code));
    }

    @Test
    void crudRoundTripWithAvatar() {
        Contacto c = new Contacto();
        c.setConCodigo("Z9");
        c.setConNombres("Nuevo Contacto");
        c.setConEmail("nuevo@x.com");
        c.setUsuId(2);
        c.setConAvatar(new byte[]{1, 2, 3, 4});
        repository.insertar(c);

        List<Contacto> found = repository.findPage(2, Usuario.Role.USER, "Nuevo", 0, 10, "con_id", true);
        assertEquals(1, found.size());
        int id = found.get(0).getConId();

        assertArrayEquals(new byte[]{1, 2, 3, 4}, repository.findAvatar(id));

        Contacto edit = repository.findById(id).orElseThrow();
        edit.setConNombres("Editado");
        repository.actualizar(edit);
        assertEquals("Editado", repository.findById(id).orElseThrow().getConNombres());

        repository.eliminar(id);
        assertTrue(repository.findById(id).isEmpty());
    }
}
