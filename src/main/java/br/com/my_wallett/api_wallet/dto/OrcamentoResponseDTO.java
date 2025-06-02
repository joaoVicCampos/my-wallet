package br.com.my_wallett.api_wallet.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrcamentoResponseDTO {
    private Long id;
    private int ano;
    private int mes;
    private BigDecimal limiteMensal;
    private Long usuarioId;
    private String usuarioNome;
    private Long categoriaId;
    private String categoriaNome;
}
