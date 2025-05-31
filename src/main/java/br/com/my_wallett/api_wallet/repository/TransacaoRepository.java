package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Transacao;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);
    List<Transacao> findByCategoriaId(Long categoriaId);

    @Query("SELECT t FROM Transacao t WHERE " +
            "(:usuarioId IS NULL OR t.usuario.id = :usuarioId) AND " +
            "(:categoriaId IS NULL OR t.categoria.id = :categoriaId) AND " +
            "(:dataInicioStr IS NULL OR t.dataInicio >= :dataInicioStr) AND " +
            "(:dataFimStr IS NULL OR t.dataFim <= :dataFimStr) AND " +
            "(:tipo IS NULL OR t.tipo = :tipo) ")

    List<Transacao> findComFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("categoriaId") Long categoriaId,
            @Param("dataInicioStr") LocalDate dataInicio,
            @Param("dataFimStr") LocalDate dataFim,
            @Param("tipo") TipoTransacao tipo);

}
