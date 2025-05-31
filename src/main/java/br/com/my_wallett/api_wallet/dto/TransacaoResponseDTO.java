package br.com.my_wallett.api_wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import lombok.Data;

@Data
public class TransacaoResponseDTO {
    private Long id;
    private BigDecimal valor;
    private String descricao;
    private TipoTransacao tipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private Long usuarioId;
    private String nomeUsuario;
    private Long categoriaId;
    private String categoriaNome;

}