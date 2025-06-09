package br.com.my_wallett.api_wallet.service;

import br.com.my_wallett.api_wallet.dto.GastoPorCategoriaDTO;
import br.com.my_wallett.api_wallet.dto.ResumoMensalDTO;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import br.com.my_wallett.api_wallet.repository.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Optional;

@Service
public class RelatorioService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Transactional(readOnly = true)
    public ResumoMensalDTO getResumoMensal(Long usuarioId, int ano, int mes) {

        LocalDate dataInicio = LocalDate.of(ano, mes, 1);
        LocalDate dataFim = dataInicio.with(TemporalAdjusters.lastDayOfMonth());

        BigDecimal totalReceitas = Optional.ofNullable(
                transacaoRepository.sumByTipoAndPeriodo(usuarioId, TipoTransacao.RECEITA, dataInicio, dataFim)
        ).orElse(BigDecimal.ZERO);
        BigDecimal totalDespesas = Optional.ofNullable(
                transacaoRepository.sumByTipoAndPeriodo(usuarioId, TipoTransacao.DESPESA, dataInicio, dataFim)
        ).orElse(BigDecimal.ZERO);

        BigDecimal saldoFinal = totalReceitas.subtract(totalDespesas);

        List<GastoPorCategoriaDTO> gastosPorCategoria = transacaoRepository.findGastosPorCategoria(usuarioId, dataInicio, dataFim);

        ResumoMensalDTO resumo = new ResumoMensalDTO();
        resumo.setTotalReceitas(totalReceitas);
        resumo.setTotalDespesas(totalDespesas);
        resumo.setSaldoFinalDoMes(saldoFinal);
        resumo.setGastosPorCategoria(gastosPorCategoria);

        return resumo;
    }
}