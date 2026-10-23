package br.com.famelsvg.biblioteca.controller;

import br.com.famelsvg.biblioteca.dto.*;
import br.com.famelsvg.biblioteca.service.CadastroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {
    private final CadastroService cadastro;

    public UsuarioController(CadastroService cadastro) {
        this.cadastro = cadastro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse criar(@Valid @RequestBody CriarUsuarioRequest r) {
        return UsuarioResponse.de(cadastro.criarUsuario(r.nome(), r.email()));
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable Long id) {
        return UsuarioResponse.de(cadastro.buscarUsuario(id));
    }
}
