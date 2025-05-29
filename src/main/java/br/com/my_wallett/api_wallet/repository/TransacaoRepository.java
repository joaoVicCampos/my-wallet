package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);
    List<Transacao> findByCategoriaId(Long categoriaId);

    @Query("SELECT t FROM Transacao t WHERE " +
            "(:usuarioId IS NULL OR t.usuario.id = :usuarioId) AND " +
            "(:categoriaId IS NULL OR t.categoria.id = :categoriaId)")
    List<Transacao> findComFiltros(
            @Param("usuarioId") Long usuarioId,
            @Param("categoriaId") Long categoriaId);
}
