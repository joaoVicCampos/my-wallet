package br.com.my_wallett.api_wallet.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "orcamentos", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "categoria_id", "mes", "ano"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "limite_mensal", nullable = false)
    private BigDecimal limiteMensal;

    @Column(nullable = false)
    private int mes;

    @Column(nullable = false)
    private int ano;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;
}
