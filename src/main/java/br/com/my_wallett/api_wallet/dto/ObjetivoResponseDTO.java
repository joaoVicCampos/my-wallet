package br.com.my_wallett.api_wallet.dto;

import br.com.my_wallett.api_wallet.model.enums.StatusObjetivo;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ObjetivoResponseDTO {
    private Long id;
    private String nome;
    private BigDecimal valorAtual;
    private BigDecimal valorMeta;
    private LocalDate dataCriacao;
    private LocalDate dataConclusaoPrevista;
    private StatusObjetivo status;
    private Long usuarioId;
}
