package br.com.my_wallett.api_wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrcamentoRequestoDTO {
    private int ano;
    private int mes;
    private BigDecimal limiteMensal;
    private Long usuarioId;
    private Long categoriaId;
}
