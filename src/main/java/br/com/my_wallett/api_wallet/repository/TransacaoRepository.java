package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
}
