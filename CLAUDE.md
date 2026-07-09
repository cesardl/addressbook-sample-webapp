# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

AddressBook is a JSF demo app (originally a Java 6 NetBeans project from ~2010, later refactored into a Maven multi-module build). It is a per-user CRUD of contacts with database-stored avatar images and JasperReports PDF generation. Two interchangeable frontends implement the same UI on top of a shared backend: one built with **RichFaces** and one with **IceFaces**. Code, comments, and log messages are largely in Spanish.

## Modules

- **be-core** — jar. All business logic, DAOs, domain, DB connection pool, utilities, and the JSF-facing base beans. Both frontends depend on this. Contains `AbstractManagerAgenda` (the bulk of the CRUD/report/avatar logic) and `ManagerLogin`.
- **fe-richfaces** — war. RichFaces 3.3.3 frontend. `ManagerAgenda` extends `AbstractManagerAgenda` and adds RichFaces-specific upload (`UploadEvent`) and PDF-streaming handlers.
- **fe-icesfaces** — war. IceFaces 1.8.2 frontend. Same pattern with an IceFaces-specific `ManagerAgenda`.

The two frontends are parallel implementations — a change to shared behavior usually belongs in `be-core`'s `AbstractManagerAgenda`, and each frontend's `ManagerAgenda` only holds framework-specific overrides.

## Architecture notes

- **JSF 1.2, session-scoped managed beans.** Beans are wired in each frontend's `WEB-INF/faces-config.xml` (`managerAgenda`, `managerLogin`), which also owns all navigation rules (outcomes like `TO_LOGIN`, `TO_ADDRESS_BOOK`, `TO_EDIT`, `TO_FIND`), the `emailValidator`, locale config (default `es`, message bundle `org.sanmarcux.util.mensajes`), and the phase listener. Views are JSPs under `src/main/webapp/` (`Login/`, `Contactos/`).
- **Auth / session.** `ManagerLogin.validarUsuario()` looks up the user via `UsuarioDAO` and stores the `Usuario` under the session attribute `"usuario"`. `AbstractManagerAgenda.getUser()` reads it back. There is no filter-based access control.
- **Authorization by role.** `Usuario.Role` is `ADMIN` or `USER`. DAO queries conditionally add `WHERE usu_id = ?` only for `USER`, so ADMIN sees all contacts.
- **DAO layer.** Plain JDBC with `PreparedStatement` in `dao/impl/*`, no ORM. Every DAO gets connections from `ConnectionPool.openConnection()`.
- **Connection pool.** `ConnectionPool` is a static Apache commons-dbcp `BasicDataSource` initialized once from the `org.sanmarcux.bd.jdbc` resource bundle (`be-core/src/main/resources/org/sanmarcux/bd/jdbc.properties`). Change DB host/credentials there.
- **Avatars** are stored as SQL `Blob` on `Contacto` and streamed to/from the DB.
- **Reports.** `AbstractManagerAgenda.getReportBytes()` fills `.jasper` templates (in each war's `webapp/reportes/`) against a live JDBC connection and exports PDF. Passwords use MySQL's legacy `PASSWORD()` hashing via `Utilities.buildMySQLPassword`.

## Build & run

Java 14 (`maven.compiler.release=14`); build with Maven. `be-core` must be installed/available for the frontends to resolve it.

```sh
mvn clean install            # build all modules + run tests + jacoco
mvn -B verify                # what CI runs
mvn -pl be-core test         # test a single module
```

Run a single test class or method (Surefire):

```sh
mvn -pl be-core test -Dtest=ManagerLoginTest
mvn -pl be-core test -Dtest=ManagerLoginTest#successLoginTest
```

Run a frontend locally on an embedded Tomcat 7 at `http://localhost:8080/addressBook`:

```sh
mvn -pl fe-richfaces  tomcat7:run
mvn -pl fe-icesfaces  tomcat7:run
```

## Database

MySQL 5.7. Bring up a local instance and load the schema (the app connects to `localhost:3310/address_book` by default — see `jdbc.properties`):

```sh
docker run --name mysql-v5_7 -p 3310:3306 --restart on-failure \
  -e MYSQL_DATABASE=address_book -e MYSQL_ROOT_PASSWORD=rootroot -e TZ=America/Lima \
  -d mysql:5.7.44 --character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci
```

- Full schema + data dump: `data/address_book_schema.sql` (also contains stored routines used by reports). The MySQL Workbench model is `data/modAddressBook.mwb`.
- The seeded dataset has ~30k contacts intentionally, so the app exhibits performance problems that are meant to be investigated/fixed.
- Default logins (see README): `admin` / `4dm1n`, `cesardl` / `123456`.

## Testing conventions

- JUnit 4 with **PowerMock + Mockito** to mock JSF statics (`FacesContext`, `ExternalContext`) — see `ManagerLoginTest`. Test doubles live in `be-core`'s `org.sanmarcux.PojoFake`.
- JaCoCo is bound into the build (`prepare-agent` / report at `prepare-package`); coverage XML at `<module>/target/site/jacoco/jacoco.xml`.
- Tests that hit DAOs need a running MySQL with the schema loaded.

## CI / quality

Travis (`.travis.yml`) runs `mvn -B verify`, then SonarCloud (`sonar-scanner`) on `master` and uploads JaCoCo coverage to CodeClimate for each module. Sonar config is in `sonar-project.properties` (note: `sonar.modules` lists only `be-core,fe-richfaces`).
