package br.com.famelsvg.biblioteca.repository;

import br.com.famelsvg.biblioteca.domain.Livro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LivroRepository extends JpaRepository<Livro, Long> {
}
