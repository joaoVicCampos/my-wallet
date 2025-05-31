package br.com.my_wallett.api_wallet.model;


import br.com.my_wallett.api_wallet.model.enums.StatusObjetivo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "objetivos")
public class Objetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "valor_meta", nullable = false)
    private BigDecimal valorMeta;

    @Column(name = "valor_atual", nullable = false)
    private BigDecimal valorAtual;

    @Column(name = "data_criacao", nullable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_conclusao_prevista")
    private LocalDate dataConclusaoPrevista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusObjetivo status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
}
