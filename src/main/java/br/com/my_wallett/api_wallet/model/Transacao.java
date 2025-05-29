package br.com.my_wallett.api_wallet.model;


import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "transacao")
public class Transacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String descricao;

    @Column
    private BigDecimal valor;

    @Column
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY) // --> Define que muitas transações podem pertencer apenas uma categoria.
    @JoinColumn(name = "categoria_id", nullable = true) // --> Relacionando Transacao com Categoria, usando o categoria_id com FK.
    private Categoria categoria;
}
