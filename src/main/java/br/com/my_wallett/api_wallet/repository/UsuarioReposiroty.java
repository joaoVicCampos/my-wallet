package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioReposiroty  extends JpaRepository<Usuario, Long> {
}
