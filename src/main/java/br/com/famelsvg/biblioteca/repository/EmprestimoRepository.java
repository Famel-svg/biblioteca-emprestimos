package br.com.famelsvg.biblioteca.repository;

import br.com.famelsvg.biblioteca.domain.Emprestimo;
import br.com.famelsvg.biblioteca.domain.StatusEmprestimo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    long countByUsuarioIdAndStatus(Long usuarioId, StatusEmprestimo status);
}
