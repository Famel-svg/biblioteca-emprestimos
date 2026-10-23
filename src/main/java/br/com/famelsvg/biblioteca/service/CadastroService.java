package br.com.famelsvg.biblioteca.service;

import br.com.famelsvg.biblioteca.domain.Exemplar;
import br.com.famelsvg.biblioteca.domain.Livro;
import br.com.famelsvg.biblioteca.domain.Usuario;
import br.com.famelsvg.biblioteca.exception.NaoEncontradoException;
import br.com.famelsvg.biblioteca.repository.ExemplarRepository;
import br.com.famelsvg.biblioteca.repository.LivroRepository;
import br.com.famelsvg.biblioteca.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CadastroService {
    private final LivroRepository livros;
    private final ExemplarRepository exemplares;
    private final UsuarioRepository usuarios;

    public CadastroService(LivroRepository livros, ExemplarRepository exemplares, UsuarioRepository usuarios) {
        this.livros = livros;
        this.exemplares = exemplares;
        this.usuarios = usuarios;
    }

    @Transactional
    public Livro criarLivro(String titulo, String autor, String isbn) {
        return livros.save(new Livro(titulo, autor, isbn));
    }

    @Transactional(readOnly = true)
    public Livro buscarLivro(Long id) {
        return livros.findById(id).orElseThrow(() -> new NaoEncontradoException("livro não encontrado"));
    }

    @Transactional
    public Exemplar criarExemplar(Long livroId) {
        return exemplares.save(new Exemplar(buscarLivro(livroId)));
    }

    @Transactional
    public Usuario criarUsuario(String nome, String email) {
        return usuarios.save(new Usuario(nome, email));
    }

    @Transactional(readOnly = true)
    public Usuario buscarUsuario(Long id) {
        return usuarios.findById(id).orElseThrow(() -> new NaoEncontradoException("usuário não encontrado"));
    }
}
