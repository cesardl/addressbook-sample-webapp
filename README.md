Address Book
============

AddressBook es un proyecto de ejemplo que realicé allá por el año 2010. En su origen fueron
dos proyectos NetBeans (Java 6) con **JSF** y frontends **RichFaces** e **IceFaces**, luego
refactorizados a un build multimódulo con Maven.

Esta versión moderniza el stack a **Spring Boot 3 (Java 17)** con un único módulo:

- **Spring MVC + Thymeleaf** para las vistas (server-side rendering).
- **Bootstrap 5 + jQuery + DataTables** (procesamiento del lado servidor) para el listado.
- **JdbcTemplate** para la persistencia (sin ORM), sobre **MySQL 8**.
- **Spring Security** con un `PasswordEncoder` que reproduce el formato legado de MySQL
  (`*SHA1(SHA1(pwd))`), de modo que los usuarios de la base de datos existente siguen
  autenticándose sin migrar datos.

Contiene un CRUD de contactos asociado al usuario logueado, con autocompletar/búsqueda,
avatares almacenados en la base de datos (BLOB) y control de acceso por rol
(ADMIN ve todos los contactos, USER sólo los suyos).

> La generación de reportes PDF con JasperReports de la versión original fue retirada.

## Requisitos

- JDK 17
- Maven 3.9+
- Docker (para levantar MySQL localmente)

## Base de datos

Levantar MySQL 8 con Docker:

```sh
docker run --name mysql-v8 -p 3310:3306 --restart on-failure \
  -e MYSQL_DATABASE=address_book -e MYSQL_ROOT_PASSWORD=rootroot -e TZ=America/Lima \
  -d mysql:8.0 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

Cargar el esquema y los datos de ejemplo:

```sh
docker exec -i mysql-v8 mysql -uroot -prootroot address_book < data/address_book_schema.sql
```

El backup incluido (`data/address_book_schema.sql`) trae ~30 contactos de ejemplo, con sus
imágenes almacenadas como BLOB. Se generó con:

```sh
mysqldump -u root -p -B --hex-blob --routines address_book > address_book_schema.sql
```

(`-B --hex-blob` es necesario para exportar correctamente los avatares almacenados en la base de
datos, y `--routines` incluye las funciones/procedimientos almacenados.)

La conexión por defecto (`localhost:3310/address_book`, usuario `root`) se configura en
`src/main/resources/application.yml`.

### Generar un dataset grande (~30k contactos)

Para reproducir el escenario de performance que motivó la paginación del lado servidor de
DataTables, aproveché la base de datos de ejemplo **_Employees_** e inserté cerca de 30k
registros como contactos.

Primero, los usuarios a partir de otra tabla de ejemplo:

```sql
insert into address_book.usuario(usu_usuario, usu_password)
select p.`name`, PASSWORD(p.`name`) from crud.tbl_person p;
```

Luego, los contactos con emails y códigos aleatorios desde el schema _Employees_:

```sql
SET @random_chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
SET @char_length = LENGTH(@random_chars);

-- Configuration for random domains
SET @domains_list = '@latin.com,@llajoo.com,@email.com,@correito.com.ar,@employees.dev,@correo.com.mx';
SET @num_domains = 6;

insert into address_book.contacto(con_nombres, con_cumpleanos, con_email, usu_id, con_codigo)
select concat(first_name, ' ', last_name), birth_date,
	lower(concat(first_name, '.', last_name,
		FLOOR(1000 + (RAND() * 8999)),
		SUBSTRING_INDEX(SUBSTRING_INDEX(@domains_list, ',', 1 + FLOOR(RAND() * @num_domains)), ',', -1)
    )),
   (
        SELECT u.usu_id
        FROM `address_book`.usuario u
        ORDER BY RAND()
        LIMIT 1
    ),
	upper(SUBSTRING(REPLACE(UUID(), '-', ''), 1, 12))
from employees;
```

> **MySQL 8**: la función `PASSWORD()` fue eliminada. El query de usuarios anterior es histórico
> (se ejecutó en MySQL 5.x); en MySQL 8 usa `Utilities.buildMySQLPassword` o valores literales
> `*HASH` (ver la sección siguiente).

## Ejecutar

```sh
mvn spring-boot:run
```

La aplicación queda en `http://localhost:8080/`.

Usuarios por defecto:

- `admin` / `4dm1n` (rol ADMIN)
- `cesardl` / `123456` (rol USER)

## Build y pruebas

```sh
mvn -B verify
```

Las pruebas unitarias (hashing de passwords, encoder, capa web con MockMvc) corren siempre.
Las pruebas de integración de la capa de persistencia usan **Testcontainers (MySQL 8)** y se
omiten automáticamente si Docker no está disponible.

## Nota sobre passwords y MySQL 8

MySQL 8 **eliminó la función `PASSWORD()`**. Los hashes ya almacenados en
`data/address_book_schema.sql` son literales con el formato `*SHA1(SHA1(pwd))`, así que el dump
carga sin problemas. Para insertar usuarios nuevos, calcula el hash en la aplicación
(`Utilities.buildMySQLPassword`) o usa valores literales, por ejemplo:

```sql
-- *SHA1(SHA1('4dm1n')) y *SHA1(SHA1('123456'))
INSERT INTO usuario(usu_usuario, usu_password, usu_role) VALUES
  ('admin',   '*68AB655AF1DDBDB3179671D16EB5B698564AC722', 'ADMIN'),
  ('cesardl', '*6BB4837EB74329105EE4568DDA7DC67ED2CA2AD9', 'USER');
```
