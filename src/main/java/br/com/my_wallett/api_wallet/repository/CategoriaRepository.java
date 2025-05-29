package br.com.my_wallett.api_wallet.repository;

import br.com.my_wallett.api_wallet.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
