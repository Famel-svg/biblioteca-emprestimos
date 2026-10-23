package br.com.famelsvg.biblioteca.repository;

import br.com.famelsvg.biblioteca.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
