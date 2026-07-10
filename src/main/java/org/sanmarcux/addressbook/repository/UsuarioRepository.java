package org.sanmarcux.addressbook.repository;

import org.sanmarcux.addressbook.domain.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Usuario> MAPPER = (rs, rowNum) -> {
        Usuario u = new Usuario();
        u.setUsuId(rs.getInt("usu_id"));
        u.setUsuUsuario(rs.getString("usu_usuario"));
        u.setUsuPassword(rs.getString("usu_password"));
        u.setRole(Usuario.Role.valueOf(rs.getString("usu_role")));
        Timestamp last = rs.getTimestamp("last_login");
        u.setLastLogin(last == null ? null : last.toLocalDateTime());
        return u;
    };

    public Optional<Usuario> findByUsername(final String username) {
        String sql = "SELECT usu_id, usu_usuario, usu_password, usu_role, last_login "
                + "FROM usuario WHERE usu_usuario = ?";
        return jdbc.query(sql, MAPPER, username).stream().findFirst();
    }

    public void actualizarUltimoAcceso(final int usuId) {
        jdbc.update("UPDATE usuario SET last_login = ? WHERE usu_id = ?",
                Timestamp.valueOf(LocalDateTime.now()), usuId);
    }
}
