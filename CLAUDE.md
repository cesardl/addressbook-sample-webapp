# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

AddressBook is a per-user contacts CRUD demo. It began in ~2010 as a JSF app (RichFaces + IceFaces
frontends on a shared `be-core`) and was **modernized to a single-module Spring Boot 3 / Java 17
application** with server-rendered Thymeleaf + Bootstrap/jQuery/DataTables. Code, comments, and UI
text are in Spanish. There is no ORM — persistence is plain `JdbcTemplate` against MySQL 8.

## Architecture

Single Maven module, base package `org.sanmarcux.addressbook`:

- **`domain/`** — `Contacto` (avatar is `byte[]`, has Bean Validation annotations) and `Usuario`
  (`Role` = `ADMIN`/`USER`).
- **`repository/`** — `@Repository` classes using `JdbcTemplate`.
  - `ContactoRepository` holds all SQL. `findPage/countVisible/countFiltered` implement **DataTables
    server-side pagination** (LIMIT/OFFSET + `LIKE` search + whitelisted ORDER BY column) — this is
    the fix for the ~30k-row performance problem. `generarCodigo()` reimplements the old MySQL
    `sp_genera_codigo()` stored function in Java. The ADMIN-vs-USER visibility filter
    (`WHERE usu_id = ?` only for USER) lives in `searchClause(...)`.
  - `UsuarioRepository` loads by username and updates `last_login`.
- **`service/ContactoService`** — `@Service`/`@Transactional`. Orchestrates the repository and
  **enforces ownership**: a USER may only read/edit/delete their own contacts (`assertOwnership`);
  ADMIN is unrestricted. Also preserves the existing avatar on update when no new file is uploaded.
- **`security/`** — Spring Security form login.
  - `MySqlPasswordEncoder` delegates to `Utilities.buildMySQLPassword` (`*SHA1(SHA1(pwd))`). This is
    **compatibility-critical**: it authenticates against the hashes already stored in the DB. Do not
    change the hashing without a data migration — `UtilitiesTest` pins the two seeded hashes.
  - `AppUserPrincipal` (a `UserDetails` carrying `usuId` + `role`), `AppUserDetailsService`,
    `SecurityConfig` (form login at `/login`, everything else authenticated),
    `LoginSuccessListener` (updates `last_login` on successful auth).
- **`web/`** — `AuthController` (`/login`, `/` → `/contactos`), `ContactoController` (list page +
  `/contactos/data` JSON endpoint for DataTables + new/edit/save/delete), `AvatarController`
  (`GET /contactos/{id}/avatar`, streams the BLOB or a default PNG).
- **Views** — `src/main/resources/templates/` (`login.html`, `contactos/lista.html`, `contactos/form.html`,
  shared `fragments/layout.html`). Bootstrap/jQuery/DataTables come from **webjars** (`/webjars/...`).
  In inlined `<script th:inline="javascript">`, avoid a literal `[[` (e.g. write `[ [2,'asc'] ]`) — it
  collides with Thymeleaf's `[[...]]` inline-expression syntax.

Controllers get the current user via `@AuthenticationPrincipal AppUserPrincipal` and pass it into the
service, which is where authorization is applied.

## Build, run, test

Java 17, Maven. Config (datasource, port, multipart limits) is in `src/main/resources/application.yml`.

```sh
mvn -B verify                 # full build + tests + jacoco
mvn spring-boot:run           # run on http://localhost:8080
mvn test -Dtest=UtilitiesTest # single test class
```

- **Unit tests always run**: `UtilitiesTest` (pins seeded password hashes), `MySqlPasswordEncoderTest`,
  and `ContactoControllerWebTest` (`@WebMvcTest` + Spring Security test; injects an `AppUserPrincipal`
  via `SecurityMockMvcRequestPostProcessors.user(...)`).
- **`ContactoRepositoryDbTest`** is a `@SpringBootTest` against **Testcontainers MySQL 8**. It is
  annotated `@Testcontainers(disabledWithoutDocker = true)`, so it runs in CI (Docker present) and is
  skipped automatically otherwise. It builds its own schema in `@BeforeEach`.

## Database

MySQL 8 (see README for the `docker run` + schema load). Default connection `localhost:3310/address_book`,
user `root`. Full schema + ~30k-row data dump: `data/address_book_schema.sql`. Default logins:
`admin`/`4dm1n` (ADMIN), `cesardl`/`123456` (USER).

Passwords are stored as `*SHA1(SHA1(pwd))`. **MySQL 8 removed the `PASSWORD()` function**, so never seed
users with `PASSWORD(...)`; use `Utilities.buildMySQLPassword` or literal `*HASH` values.

## CI

`.github/workflows/ci.yml` runs `mvn -B verify` on Java 17 (Docker is available on GitHub-hosted
runners, so the Testcontainers test executes there). The old Travis/SonarCloud/CodeClimate setup was
removed.
