package org.sanmarcux.addressbook.web;

import org.sanmarcux.addressbook.security.AppUserPrincipal;
import org.sanmarcux.addressbook.service.ContactoService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Controller
public class AvatarController {

    private final ContactoService contactoService;

    public AvatarController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping("/contactos/{id}/avatar")
    public ResponseEntity<byte[]> avatar(@PathVariable int id,
                                         @AuthenticationPrincipal AppUserPrincipal user) throws IOException {
        byte[] bytes = contactoService.avatar(id, user);
        if (bytes == null || bytes.length == 0) {
            bytes = defaultAvatar();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .cacheControl(CacheControl.maxAge(1, TimeUnit.HOURS).cachePrivate())
                .body(bytes);
    }

    private byte[] defaultAvatar() throws IOException {
        try (InputStream in = new ClassPathResource("static/images/avatar-default.png").getInputStream()) {
            return in.readAllBytes();
        }
    }
}
