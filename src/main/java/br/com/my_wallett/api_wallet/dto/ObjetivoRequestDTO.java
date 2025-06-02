package br.com.my_wallett.api_wallet.dto;

import br.com.my_wallett.api_wallet.model.enums.StatusObjetivo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ObjetivoRequestDTO {
    private String nome;
    private BigDecimal valorMeta;
    private LocalDate dataConclusaoPrevista;
    private Long usuarioId;
}
