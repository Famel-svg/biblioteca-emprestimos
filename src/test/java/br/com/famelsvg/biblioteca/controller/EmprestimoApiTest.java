package br.com.famelsvg.biblioteca.controller;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.famelsvg.biblioteca.dto.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmprestimoApiTest {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired TestRestTemplate http;

    private Long novoLivro() {
        var r = http.postForEntity("/livros",
                new CriarLivroRequest("Livro Teste", "Autora Ficticia", UUID.randomUUID().toString()),
                LivroResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return r.getBody().id();
    }

    private Long novoExemplar(Long livroId) {
        var r = http.postForEntity("/livros/" + livroId + "/exemplares", null, ExemplarResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(r.getBody().status().name()).isEqualTo("DISPONIVEL");
        return r.getBody().id();
    }

    private Long novoUsuario() {
        var r = http.postForEntity("/usuarios",
                new CriarUsuarioRequest("Fulano Ficticio", UUID.randomUUID() + "@exemplo.com"),
                UsuarioResponse.class);
        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return r.getBody().id();
    }

    private ResponseEntity<Map> emprestar(Long usuarioId, Long exemplarId) {
        return http.postForEntity("/emprestimos", new EmprestimoRequest(usuarioId, exemplarId),
                Map.class);
    }

    @Test
    void fluxoCompleto() {
        Long livro = novoLivro();
        assertThat(http.getForEntity("/livros/" + livro, LivroResponse.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);
        Long exemplar = novoExemplar(livro);
        Long usuario = novoUsuario();
        assertThat(http.getForEntity("/usuarios/" + usuario, UsuarioResponse.class).getStatusCode())
                .isEqualTo(HttpStatus.OK);

        var emp = emprestar(usuario, exemplar);
        assertThat(emp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        Long id = ((Number) emp.getBody().get("id")).longValue();
        assertThat(LocalDate.parse((String) emp.getBody().get("dataPrevista")))
                .isEqualTo(LocalDate.parse((String) emp.getBody().get("dataEmprestimo")).plusDays(14));

        assertThat(emprestar(usuario, exemplar).getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        var renovado = http.postForEntity("/emprestimos/" + id + "/renovacao", null, EmprestimoResponse.class);
        assertThat(renovado.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(renovado.getBody().renovacoes()).isEqualTo(1);

        var segunda = http.postForEntity("/emprestimos/" + id + "/renovacao", null, Map.class);
        assertThat(segunda.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        var devolvido = http.postForEntity("/emprestimos/" + id + "/devolucao", null, EmprestimoResponse.class);
        assertThat(devolvido.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(devolvido.getBody().multa()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(devolvido.getBody().status().name()).isEqualTo("DEVOLVIDO");

        // exemplar volta a ficar disponível
        assertThat(emprestar(usuario, exemplar).getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void naoEncontrado() {
        assertThat(http.getForEntity("/livros/999999", Map.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(http.getForEntity("/usuarios/999999", Map.class).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(http.postForEntity("/livros/999999/exemplares", null, Map.class).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(http.postForEntity("/emprestimos/999999/devolucao", null, Map.class).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(http.postForEntity("/emprestimos/999999/renovacao", null, Map.class).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(emprestar(999999L, 999999L).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void corpoInvalido() {
        assertThat(http.postForEntity("/livros", new CriarLivroRequest("", null, null), Map.class).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(http.postForEntity("/usuarios", new CriarUsuarioRequest("Fulano", "nao-e-email"), Map.class)
                .getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(http.postForEntity("/emprestimos", new EmprestimoRequest(0L, null), Map.class).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void isbnDuplicadoRetornaConflito() {
        var req = new CriarLivroRequest("Dup", "Autor", "isbn-dup-" + UUID.randomUUID());
        assertThat(http.postForEntity("/livros", req, Map.class).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(http.postForEntity("/livros", req, Map.class).getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void limiteDeTresEmprestimos() {
        Long livro = novoLivro();
        Long usuario = novoUsuario();
        for (int i = 0; i < 3; i++) {
            assertThat(emprestar(usuario, novoExemplar(livro)).getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }
        assertThat(emprestar(usuario, novoExemplar(livro)).getStatusCode())
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
