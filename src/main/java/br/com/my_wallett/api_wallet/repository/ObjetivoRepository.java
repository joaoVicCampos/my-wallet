package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Objetivo;
import br.com.my_wallett.api_wallet.model.enums.StatusObjetivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObjetivoRepository extends JpaRepository<Objetivo, Long> {

    List<Objetivo> findByUsuarioId(Long usuarioId);
    List<Objetivo> findByUsuarioIdAndStatus(Long usuarioId, StatusObjetivo status);
}
