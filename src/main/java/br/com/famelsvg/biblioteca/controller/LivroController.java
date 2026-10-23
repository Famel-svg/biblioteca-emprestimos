package br.com.famelsvg.biblioteca.controller;

import br.com.famelsvg.biblioteca.dto.*;
import br.com.famelsvg.biblioteca.service.CadastroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
public class LivroController {
    private final CadastroService cadastro;

    public LivroController(CadastroService cadastro) {
        this.cadastro = cadastro;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroResponse criar(@Valid @RequestBody CriarLivroRequest r) {
        return LivroResponse.de(cadastro.criarLivro(r.titulo(), r.autor(), r.isbn()));
    }

    @GetMapping("/{id}")
    public LivroResponse buscar(@PathVariable Long id) {
        return LivroResponse.de(cadastro.buscarLivro(id));
    }

    @PostMapping("/{id}/exemplares")
    @ResponseStatus(HttpStatus.CREATED)
    public ExemplarResponse criarExemplar(@PathVariable Long id) {
        return ExemplarResponse.de(cadastro.criarExemplar(id));
    }
}
