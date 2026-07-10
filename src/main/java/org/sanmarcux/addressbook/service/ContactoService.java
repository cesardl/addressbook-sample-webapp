package org.sanmarcux.addressbook.service;

import org.sanmarcux.addressbook.domain.Contacto;
import org.sanmarcux.addressbook.repository.ContactoRepository;
import org.sanmarcux.addressbook.security.AppUserPrincipal;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio de la agenda. Aplica la autorización por propietario:
 * un usuario con rol USER sólo puede ver/editar sus propios contactos, ADMIN todos.
 */
@Service
public class ContactoService {

    private final ContactoRepository repository;

    public ContactoService(ContactoRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public long countVisible(final AppUserPrincipal user) {
        return repository.countVisible(user.getUsuId(), user.getRole());
    }

    @Transactional(readOnly = true)
    public long countFiltered(final AppUserPrincipal user, final String search) {
        return repository.countFiltered(user.getUsuId(), user.getRole(), search);
    }

    @Transactional(readOnly = true)
    public List<Contacto> page(final AppUserPrincipal user, final String search,
                               final int start, final int length,
                               final String orderColumn, final boolean asc) {
        return repository.findPage(user.getUsuId(), user.getRole(), search, start, length, orderColumn, asc);
    }

    @Transactional(readOnly = true)
    public Contacto findForEdit(final int id, final AppUserPrincipal user) {
        Contacto contacto = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contacto no encontrado: " + id));
        assertOwnership(contacto.getUsuId(), user);
        return contacto;
    }

    @Transactional(readOnly = true)
    public byte[] avatar(final int id, final AppUserPrincipal user) {
        Contacto contacto = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contacto no encontrado: " + id));
        assertOwnership(contacto.getUsuId(), user);
        return contacto.getConAvatar();
    }

    public String nuevoCodigo() {
        return repository.generarCodigo();
    }

    @Transactional
    public void guardar(final Contacto contacto, final byte[] nuevoAvatar,
                        final boolean quitarAvatar, final AppUserPrincipal user) {
        if (contacto.getConId() == 0) {
            // Alta: el contacto pertenece al usuario logueado.
            contacto.setUsuId(user.getUsuId());
            if (contacto.getConCodigo() == null || contacto.getConCodigo().isBlank()) {
                contacto.setConCodigo(repository.generarCodigo());
            }
            contacto.setConAvatar(quitarAvatar ? null : nuevoAvatar);
            repository.insertar(contacto);
        } else {
            Contacto actual = repository.findById(contacto.getConId())
                    .orElseThrow(() -> new IllegalArgumentException("Contacto no encontrado: " + contacto.getConId()));
            assertOwnership(actual.getUsuId(), user);
            contacto.setUsuId(actual.getUsuId());

            // Preserva el avatar existente salvo que se suba uno nuevo o se pida quitarlo.
            if (quitarAvatar) {
                contacto.setConAvatar(null);
            } else if (nuevoAvatar != null && nuevoAvatar.length > 0) {
                contacto.setConAvatar(nuevoAvatar);
            } else {
                contacto.setConAvatar(repository.findAvatar(contacto.getConId()));
            }
            repository.actualizar(contacto);
        }
    }

    @Transactional
    public void eliminar(final int id, final AppUserPrincipal user) {
        Optional<Contacto> contacto = repository.findById(id);
        if (contacto.isEmpty()) {
            return;
        }
        assertOwnership(contacto.get().getUsuId(), user);
        repository.eliminar(id);
    }

    private void assertOwnership(final int ownerId, final AppUserPrincipal user) {
        if (!user.isAdmin() && ownerId != user.getUsuId()) {
            throw new AccessDeniedException("No autorizado para acceder a este contacto");
        }
    }
}
