package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Orcamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface OrcamentoRepository extends JpaRepository<Orcamento, Long>, JpaSpecificationExecutor<Orcamento> {
    List<Orcamento> findByUsuarioId(Long usuarioId);
    List<Orcamento> findByUsuarioIdAndMesAndAno(Long usuarioId, int mes, int ano);
    Optional<Orcamento> findByUsuarioIdAndCategoriaIdAndMesAndAno(Long usuarioId, Long categoriaId, int mes, int ano);
    Optional<Orcamento> findByIdAndUsuarioId(Long orcamentoId, Long usuarioId);
}
