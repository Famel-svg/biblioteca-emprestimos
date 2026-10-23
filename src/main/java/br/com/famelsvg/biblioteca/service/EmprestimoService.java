package br.com.famelsvg.biblioteca.service;

import br.com.famelsvg.biblioteca.domain.*;
import br.com.famelsvg.biblioteca.exception.NaoEncontradoException;
import br.com.famelsvg.biblioteca.exception.NegocioException;
import br.com.famelsvg.biblioteca.repository.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmprestimoService {
    private final EmprestimoRepository emprestimos;
    private final ExemplarRepository exemplares;
    private final UsuarioRepository usuarios;
    private final Clock clock;

    public EmprestimoService(EmprestimoRepository emprestimos, ExemplarRepository exemplares,
                             UsuarioRepository usuarios, Clock clock) {
        this.emprestimos = emprestimos;
        this.exemplares = exemplares;
        this.usuarios = usuarios;
        this.clock = clock;
    }

    @Transactional
    public Emprestimo emprestar(Long usuarioId, Long exemplarId) {
        Usuario usuario = usuarios.findById(usuarioId)
                .orElseThrow(() -> new NaoEncontradoException("usuário não encontrado"));
        Exemplar exemplar = exemplares.findById(exemplarId)
                .orElseThrow(() -> new NaoEncontradoException("exemplar não encontrado"));
        if (exemplar.getStatus() != StatusExemplar.DISPONIVEL) {
            throw new NegocioException("exemplar indisponível");
        }
        if (emprestimos.countByUsuarioIdAndStatus(usuarioId, StatusEmprestimo.ATIVO)
                >= RegrasEmprestimo.LIMITE_EMPRESTIMOS_ATIVOS) {
            throw new NegocioException("limite de empréstimos atingido");
        }
        LocalDate hoje = LocalDate.now(clock);
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        exemplares.save(exemplar);
        return emprestimos.save(new Emprestimo(exemplar, usuario, hoje, hoje.plusDays(RegrasEmprestimo.PRAZO_DIAS)));
    }

    @Transactional
    public Emprestimo devolver(Long emprestimoId) {
        Emprestimo e = buscarAtivo(emprestimoId);
        LocalDate hoje = LocalDate.now(clock);
        long atraso = Math.max(0, ChronoUnit.DAYS.between(e.getDataPrevista(), hoje));
        e.setMulta(RegrasEmprestimo.MULTA_POR_DIA.multiply(BigDecimal.valueOf(atraso))
                .setScale(2, RoundingMode.HALF_UP));
        e.setDataDevolucao(hoje);
        e.setStatus(StatusEmprestimo.DEVOLVIDO);
        e.getExemplar().setStatus(StatusExemplar.DISPONIVEL);
        exemplares.save(e.getExemplar());
        return emprestimos.save(e);
    }

    @Transactional
    public Emprestimo renovar(Long emprestimoId) {
        Emprestimo e = buscarAtivo(emprestimoId);
        if (LocalDate.now(clock).isAfter(e.getDataPrevista())) {
            throw new NegocioException("empréstimo atrasado não pode ser renovado");
        }
        if (e.getRenovacoes() >= RegrasEmprestimo.MAX_RENOVACOES) {
            throw new NegocioException("limite de renovações atingido");
        }
        e.setDataPrevista(e.getDataPrevista().plusDays(RegrasEmprestimo.PRAZO_RENOVACAO_DIAS));
        e.setRenovacoes(e.getRenovacoes() + 1);
        return emprestimos.save(e);
    }

    private Emprestimo buscarAtivo(Long id) {
        Emprestimo e = emprestimos.findById(id)
                .orElseThrow(() -> new NaoEncontradoException("empréstimo não encontrado"));
        if (e.getStatus() != StatusEmprestimo.ATIVO) {
            throw new NegocioException("empréstimo não está ativo");
        }
        return e;
    }
}
