package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Transacao;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    List<Transacao> findByCategoriaId(Long categoriaId);

    Optional<Transacao> findByIdAndUsuarioId(Long transacaoId, Long usuarioId);

    @Query("SELECT t FROM Transacao t WHERE " +
            "t.usuario.id = :usuarioId AND " +
            "(:categoriaId IS NULL OR t.categoria.id = :categoriaId) AND " +
            "(:dataInicioFiltro IS NULL OR t.dataInicio >= :dataInicioFiltro) AND " +
            "(:dataFimFiltro IS NULL OR t.dataFim <= :dataFimFiltro) AND " +
            "(:tipo IS NULL OR t.tipo = :tipo)")
    List<Transacao> findComFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("categoriaId") Long categoriaId,
            @Param("dataInicioFiltro") String dataInicio,
            @Param("dataFimFiltro") String dataFim,
            @Param("tipo") TipoTransacao tipo);

}
