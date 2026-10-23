package br.com.famelsvg.biblioteca.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "exemplar")
public class Exemplar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "livro_id", nullable = false)
    private Livro livro;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusExemplar status = StatusExemplar.DISPONIVEL;

    protected Exemplar() {
    }

    public Exemplar(Livro livro) {
        this.livro = livro;
    }

    public Long getId() { return id; }
    public Livro getLivro() { return livro; }
    public StatusExemplar getStatus() { return status; }
    public void setStatus(StatusExemplar status) { this.status = status; }
}
