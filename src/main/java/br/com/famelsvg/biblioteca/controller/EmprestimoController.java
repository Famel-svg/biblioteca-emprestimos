package br.com.famelsvg.biblioteca.controller;

import br.com.famelsvg.biblioteca.dto.*;
import br.com.famelsvg.biblioteca.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emprestimos")
public class EmprestimoController {
    private final EmprestimoService service;

    public EmprestimoController(EmprestimoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmprestimoResponse emprestar(@Valid @RequestBody EmprestimoRequest r) {
        return EmprestimoResponse.de(service.emprestar(r.usuarioId(), r.exemplarId()));
    }

    @PostMapping("/{id}/devolucao")
    public EmprestimoResponse devolver(@PathVariable Long id) {
        return EmprestimoResponse.de(service.devolver(id));
    }

    @PostMapping("/{id}/renovacao")
    public EmprestimoResponse renovar(@PathVariable Long id) {
        return EmprestimoResponse.de(service.renovar(id));
    }
}
