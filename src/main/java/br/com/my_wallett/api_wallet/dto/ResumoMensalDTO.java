package br.com.my_wallett.api_wallet.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class ResumoMensalDTO {
    private BigDecimal totalReceitas;
    private BigDecimal totalDespesas;
    private BigDecimal saldoFinalDoMes;
    private List<GastoPorCategoriaDTO> gastosPorCategoria;
}