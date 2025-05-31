package br.com.my_wallett.api_wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import lombok.Data;

@Data
public class TransacaoRequestDTO {
    private BigDecimal valor;
    private String descricao;
    private TipoTransacao tipo;
    private Long usuarioId; // --> id de usuario ao qual a transação pertence
    private Long categoriaId; // --> id para associar categorias e transações
    private LocalDate dataInicio;
    private LocalDate dataFim;

}
