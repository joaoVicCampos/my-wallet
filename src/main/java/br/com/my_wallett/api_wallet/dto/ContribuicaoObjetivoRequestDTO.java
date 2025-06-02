package br.com.my_wallett.api_wallet.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ContribuicaoObjetivoRequestDTO {
    private BigDecimal valorContribuicao;
}
