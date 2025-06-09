package br.com.my_wallett.api_wallet.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoPorCategoriaDTO {
    private String nomeCategoria;
    private BigDecimal valorTotalGasto;
}