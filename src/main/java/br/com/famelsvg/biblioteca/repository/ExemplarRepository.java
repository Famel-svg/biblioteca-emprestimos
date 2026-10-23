package br.com.famelsvg.biblioteca.repository;

import br.com.famelsvg.biblioteca.domain.Exemplar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
}
