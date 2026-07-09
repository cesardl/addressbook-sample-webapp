package org.sanmarcux.addressbook.repository;

import org.sanmarcux.addressbook.domain.Contacto;
import org.sanmarcux.addressbook.domain.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Repository
public class ContactoRepository {

    /** Columnas ordenables permitidas (whitelist para evitar inyección en el ORDER BY). */
    private static final List<String> SORTABLE = List.of(
            "con_codigo", "con_nombres", "con_telefono", "con_email", "con_cumpleanos");

    private static final String COLS =
            "con_id, con_codigo, con_nombres, con_telefono, con_email, con_cumpleanos, usu_id";

    private final JdbcTemplate jdbc;

    public ContactoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** RowMapper sin avatar (para listados). */
    private static final RowMapper<Contacto> LIST_MAPPER = (rs, rowNum) -> {
        Contacto c = new Contacto();
        c.setConId(rs.getInt("con_id"));
        c.setConCodigo(rs.getString("con_codigo"));
        c.setConNombres(rs.getString("con_nombres"));
        c.setConTelefono(rs.getString("con_telefono"));
        c.setConEmail(rs.getString("con_email"));
        Date bd = rs.getDate("con_cumpleanos");
        c.setConCumpleanos(bd == null ? null : bd.toLocalDate());
        c.setUsuId(rs.getInt("usu_id"));
        return c;
    };

    /** RowMapper con avatar (para el detalle de un contacto). */
    private static final RowMapper<Contacto> DETAIL_MAPPER = (rs, rowNum) -> {
        Contacto c = LIST_MAPPER.mapRow(rs, rowNum);
        c.setConAvatar(rs.getBytes("con_avatar"));
        return c;
    };

    // ---------- Server-side DataTables ----------

    /** Total de contactos visibles para el usuario (ADMIN ve todos). */
    public long countVisible(final int usuId, final Usuario.Role role) {
        if (Usuario.Role.ADMIN.equals(role)) {
            return jdbc.queryForObject("SELECT COUNT(*) FROM contacto", Long.class);
        }
        return jdbc.queryForObject("SELECT COUNT(*) FROM contacto WHERE usu_id = ?", Long.class, usuId);
    }

    /** Total tras aplicar el filtro de búsqueda. */
    public long countFiltered(final int usuId, final Usuario.Role role, final String search) {
        if (search == null || search.isBlank()) {
            return countVisible(usuId, role);
        }
        List<Object> args = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM contacto WHERE " + searchClause(usuId, role, search, args);
        return jdbc.queryForObject(sql, Long.class, args.toArray());
    }

    /** Página de resultados para el listado / autocomplete. */
    public List<Contacto> findPage(final int usuId, final Usuario.Role role, final String search,
                                   final int start, final int length,
                                   final String orderColumn, final boolean asc) {
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT ").append(COLS).append(" FROM contacto");

        String where = searchClause(usuId, role, search, args);
        if (!where.isEmpty()) {
            sql.append(" WHERE ").append(where);
        }

        String col = SORTABLE.contains(orderColumn) ? orderColumn : "con_id";
        sql.append(" ORDER BY ").append(col).append(asc ? " ASC" : " DESC");
        sql.append(" LIMIT ? OFFSET ?");
        args.add(length);
        args.add(Math.max(start, 0));

        return jdbc.query(sql.toString(), LIST_MAPPER, args.toArray());
    }

    /** Construye la condición WHERE combinando filtro por rol y término de búsqueda. */
    private String searchClause(final int usuId, final Usuario.Role role,
                                final String search, final List<Object> args) {
        List<String> conditions = new ArrayList<>();

        if (Usuario.Role.USER.equals(role)) {
            conditions.add("usu_id = ?");
            args.add(usuId);
        }
        if (search != null && !search.isBlank()) {
            conditions.add("(con_nombres LIKE ? OR con_email LIKE ? OR con_codigo LIKE ? OR con_telefono LIKE ?)");
            String like = "%" + search.trim() + "%";
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
        }
        return String.join(" AND ", conditions);
    }

    // ---------- CRUD ----------

    public Optional<Contacto> findById(final int idContacto) {
        String sql = "SELECT " + COLS + ", con_avatar FROM contacto WHERE con_id = ?";
        return jdbc.query(sql, DETAIL_MAPPER, idContacto).stream().findFirst();
    }

    public byte[] findAvatar(final int idContacto) {
        List<byte[]> result = jdbc.query(
                "SELECT con_avatar FROM contacto WHERE con_id = ?",
                (rs, rowNum) -> rs.getBytes("con_avatar"), idContacto);
        return result.isEmpty() ? null : result.get(0);
    }

    /**
     * Genera un código corto único para un nuevo contacto.
     * Reemplaza la función almacenada {@code sp_genera_codigo()} de MySQL por lógica en Java.
     */
    public String generarCodigo() {
        for (int attempt = 0; attempt < 50; attempt++) {
            String codigo = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
            Integer count = jdbc.queryForObject(
                    "SELECT COUNT(*) FROM contacto WHERE con_codigo = ?", Integer.class, codigo);
            if (count != null && count == 0) {
                return codigo;
            }
        }
        // Fallback improbable: usa una porción de nanotiempo.
        return String.valueOf(System.nanoTime() % 100000);
    }

    public void insertar(final Contacto c) {
        jdbc.update("INSERT INTO contacto(con_codigo, con_nombres, con_telefono, con_avatar, "
                        + "con_email, con_cumpleanos, usu_id) VALUES(?, ?, ?, ?, ?, ?, ?)",
                ps -> {
                    ps.setString(1, c.getConCodigo());
                    ps.setString(2, c.getConNombres());
                    ps.setString(3, emptyToNull(c.getConTelefono()));
                    ps.setBytes(4, c.getConAvatar());
                    ps.setString(5, emptyToNull(c.getConEmail()));
                    setDate(ps, 6, c.getConCumpleanos());
                    ps.setInt(7, c.getUsuId());
                });
    }

    public void actualizar(final Contacto c) {
        jdbc.update("UPDATE contacto SET con_codigo = ?, con_nombres = ?, con_telefono = ?, "
                        + "con_avatar = ?, con_email = ?, con_cumpleanos = ? WHERE con_id = ?",
                ps -> {
                    ps.setString(1, c.getConCodigo());
                    ps.setString(2, c.getConNombres());
                    ps.setString(3, emptyToNull(c.getConTelefono()));
                    ps.setBytes(4, c.getConAvatar());
                    ps.setString(5, emptyToNull(c.getConEmail()));
                    setDate(ps, 6, c.getConCumpleanos());
                    ps.setInt(7, c.getConId());
                });
    }

    public void eliminar(final int idContacto) {
        jdbc.update("DELETE FROM contacto WHERE con_id = ?", idContacto);
    }

    /** Actualiza sólo el avatar (usado cuando el formulario no reenvía la imagen). */
    public void actualizarAvatar(final int idContacto, final byte[] avatar) {
        jdbc.update("UPDATE contacto SET con_avatar = ? WHERE con_id = ?", avatar, idContacto);
    }

    public Map<String, Object> ownerInfo(final int idContacto) {
        return jdbc.queryForMap("SELECT usu_id FROM contacto WHERE con_id = ?", idContacto);
    }

    private static String emptyToNull(final String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private static void setDate(final java.sql.PreparedStatement ps, final int idx, final LocalDate date)
            throws java.sql.SQLException {
        if (date == null) {
            ps.setNull(idx, Types.DATE);
        } else {
            ps.setDate(idx, Date.valueOf(date));
        }
    }
}
