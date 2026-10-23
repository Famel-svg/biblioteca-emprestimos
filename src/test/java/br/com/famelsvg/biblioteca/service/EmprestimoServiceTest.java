package br.com.famelsvg.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.famelsvg.biblioteca.domain.*;
import br.com.famelsvg.biblioteca.exception.NaoEncontradoException;
import br.com.famelsvg.biblioteca.exception.NegocioException;
import br.com.famelsvg.biblioteca.repository.*;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {
    private static final LocalDate HOJE = LocalDate.of(2026, 3, 10);

    @Mock EmprestimoRepository emprestimos;
    @Mock ExemplarRepository exemplares;
    @Mock UsuarioRepository usuarios;

    EmprestimoService service;
    Usuario usuario = new Usuario("Ana", "ana@example.com");
    Exemplar exemplar = new Exemplar(new Livro("Dom Casmurro", "Machado de Assis", "978-0000000001"));

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(HOJE.atStartOfDay().toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
        service = new EmprestimoService(emprestimos, exemplares, usuarios, clock);
    }

    private Emprestimo ativoComPrevista(LocalDate prevista) {
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        Emprestimo e = new Emprestimo(exemplar, usuario, prevista.minusDays(14), prevista);
        when(emprestimos.findById(1L)).thenReturn(Optional.of(e));
        return e;
    }

    @Test
    void emprestaExemplarDisponivel() {
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(exemplares.findById(2L)).thenReturn(Optional.of(exemplar));
        when(emprestimos.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(2L);
        when(emprestimos.save(any(Emprestimo.class))).thenAnswer(i -> i.getArgument(0));

        Emprestimo e = service.emprestar(1L, 2L);

        assertThat(e.getDataEmprestimo()).isEqualTo(HOJE);
        assertThat(e.getDataPrevista()).isEqualTo(HOJE.plusDays(14));
        assertThat(e.getStatus()).isEqualTo(StatusEmprestimo.ATIVO);
        assertThat(e.getMulta()).isEqualByComparingTo("0");
        assertThat(exemplar.getStatus()).isEqualTo(StatusExemplar.EMPRESTADO);
    }

    @Test
    void recusaExemplarIndisponivel() {
        exemplar.setStatus(StatusExemplar.EMPRESTADO);
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(exemplares.findById(2L)).thenReturn(Optional.of(exemplar));

        assertThatThrownBy(() -> service.emprestar(1L, 2L))
                .isInstanceOf(NegocioException.class).hasMessage("exemplar indisponível");
    }

    @Test
    void recusaQuandoLimiteDeTresAtingido() {
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(exemplares.findById(2L)).thenReturn(Optional.of(exemplar));
        when(emprestimos.countByUsuarioIdAndStatus(1L, StatusEmprestimo.ATIVO)).thenReturn(3L);

        assertThatThrownBy(() -> service.emprestar(1L, 2L))
                .isInstanceOf(NegocioException.class).hasMessage("limite de empréstimos atingido");
        verify(emprestimos, never()).save(any());
    }

    @Test
    void usuarioInexistente() {
        when(usuarios.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.emprestar(1L, 2L)).isInstanceOf(NaoEncontradoException.class);
    }

    @Test
    void exemplarInexistente() {
        when(usuarios.findById(1L)).thenReturn(Optional.of(usuario));
        when(exemplares.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.emprestar(1L, 2L)).isInstanceOf(NaoEncontradoException.class);
    }

    @Test
    void devolucaoNoPrazoSemMulta() {
        Emprestimo e = ativoComPrevista(HOJE);
        when(emprestimos.save(any(Emprestimo.class))).thenAnswer(i -> i.getArgument(0));

        service.devolver(1L);

        assertThat(e.getMulta()).isEqualByComparingTo("0.00");
        assertThat(e.getStatus()).isEqualTo(StatusEmprestimo.DEVOLVIDO);
        assertThat(e.getDataDevolucao()).isEqualTo(HOJE);
        assertThat(exemplar.getStatus()).isEqualTo(StatusExemplar.DISPONIVEL);
    }

    @Test
    void devolucaoComTresDiasDeAtrasoCobraSeis() {
        Emprestimo e = ativoComPrevista(HOJE.minusDays(3));
        when(emprestimos.save(any(Emprestimo.class))).thenAnswer(i -> i.getArgument(0));

        service.devolver(1L);

        assertThat(e.getMulta()).isEqualTo(new BigDecimal("6.00"));
    }

    @Test
    void naoDevolveDuasVezes() {
        Emprestimo e = ativoComPrevista(HOJE);
        e.setStatus(StatusEmprestimo.DEVOLVIDO);

        assertThatThrownBy(() -> service.devolver(1L)).isInstanceOf(NegocioException.class);
    }

    @Test
    void renovaEmprestimoNoPrazo() {
        Emprestimo e = ativoComPrevista(HOJE);
        when(emprestimos.save(any(Emprestimo.class))).thenAnswer(i -> i.getArgument(0));

        service.renovar(1L);

        assertThat(e.getDataPrevista()).isEqualTo(HOJE.plusDays(14));
        assertThat(e.getRenovacoes()).isEqualTo(1);
    }

    @Test
    void renovacaoBloqueadaPorAtraso() {
        ativoComPrevista(HOJE.minusDays(1));

        assertThatThrownBy(() -> service.renovar(1L)).isInstanceOf(NegocioException.class);
    }

    @Test
    void renovacaoBloqueadaPorJaTerRenovado() {
        Emprestimo e = ativoComPrevista(HOJE);
        e.setRenovacoes(1);

        assertThatThrownBy(() -> service.renovar(1L)).isInstanceOf(NegocioException.class);
    }
}
