package org.sanmarcux.addressbook.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.sanmarcux.addressbook.domain.Contacto;
import org.sanmarcux.addressbook.security.AppUserPrincipal;
import org.sanmarcux.addressbook.service.ContactoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/contactos")
public class ContactoController {

    /** Mapea el índice de columna de DataTables a la columna ordenable de BD (null = no ordenable). */
    private static final String[] ORDER_BY_INDEX =
            {null, "con_codigo", "con_nombres", "con_telefono", "con_email", "con_cumpleanos", null};

    private final ContactoService contactoService;

    public ContactoController(ContactoService contactoService) {
        this.contactoService = contactoService;
    }

    @GetMapping
    public String lista(@AuthenticationPrincipal AppUserPrincipal user, Model model) {
        model.addAttribute("admin", user.isAdmin());
        return "contactos/lista";
    }

    /** Endpoint de datos para DataTables con procesamiento del lado servidor (también sirve de autocomplete). */
    @GetMapping(value = "/data", produces = "application/json")
    @ResponseBody
    public Map<String, Object> data(@AuthenticationPrincipal AppUserPrincipal user, HttpServletRequest req) {
        int draw = intParam(req, "draw", 1);
        int start = intParam(req, "start", 0);
        int length = intParam(req, "length", 10);
        String search = req.getParameter("search[value]");

        int orderIdx = intParam(req, "order[0][column]", 0);
        boolean asc = !"desc".equalsIgnoreCase(req.getParameter("order[0][dir]"));
        String orderColumn = (orderIdx >= 0 && orderIdx < ORDER_BY_INDEX.length) ? ORDER_BY_INDEX[orderIdx] : null;

        List<Contacto> page = contactoService.page(user, search, start, length, orderColumn, asc);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (Contacto c : page) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", c.getConId());
            row.put("codigo", c.getConCodigo());
            row.put("nombres", c.getConNombres());
            row.put("telefono", c.getConTelefono());
            row.put("email", c.getConEmail());
            row.put("cumpleanos", c.getConStrcumpl());
            rows.add(row);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("draw", draw);
        response.put("recordsTotal", contactoService.countVisible(user));
        response.put("recordsFiltered", contactoService.countFiltered(user, search));
        response.put("data", rows);
        return response;
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Contacto contacto = new Contacto();
        contacto.setConCodigo(contactoService.nuevoCodigo());
        model.addAttribute("contacto", contacto);
        model.addAttribute("titulo", "Insertar contacto");
        return "contactos/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable int id, @AuthenticationPrincipal AppUserPrincipal user, Model model) {
        model.addAttribute("contacto", contactoService.findForEdit(id, user));
        model.addAttribute("titulo", "Editar contacto");
        return "contactos/form";
    }

    @PostMapping
    public String guardar(@Valid @ModelAttribute("contacto") Contacto contacto,
                          BindingResult binding,
                          @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile,
                          @RequestParam(value = "quitarAvatar", defaultValue = "false") boolean quitarAvatar,
                          @AuthenticationPrincipal AppUserPrincipal user,
                          Model model,
                          RedirectAttributes redirect) throws IOException {
        if (binding.hasErrors()) {
            model.addAttribute("titulo", contacto.getConId() == 0 ? "Insertar contacto" : "Editar contacto");
            return "contactos/form";
        }
        byte[] avatarBytes = (avatarFile != null && !avatarFile.isEmpty()) ? avatarFile.getBytes() : null;
        contactoService.guardar(contacto, avatarBytes, quitarAvatar, user);
        redirect.addFlashAttribute("mensaje", "Contacto guardado correctamente");
        return "redirect:/contactos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable int id, @AuthenticationPrincipal AppUserPrincipal user,
                           RedirectAttributes redirect) {
        contactoService.eliminar(id, user);
        redirect.addFlashAttribute("mensaje", "Contacto eliminado");
        return "redirect:/contactos";
    }

    private static int intParam(HttpServletRequest req, String name, int def) {
        String v = req.getParameter(name);
        if (v == null || v.isBlank()) {
            return def;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return def;
        }
    }
}
