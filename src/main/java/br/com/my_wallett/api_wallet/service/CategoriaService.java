package br.com.my_wallett.api_wallet.service;

import br.com.my_wallett.api_wallet.dto.CategoriaRequestDTO;
import br.com.my_wallett.api_wallet.dto.CategoriaResponseDTO;
import br.com.my_wallett.api_wallet.model.Categoria;
import br.com.my_wallett.api_wallet.repository.CategoriaRepository;
import br.com.my_wallett.api_wallet.repository.UsuarioReposiroty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    CategoriaRepository categoriaRepository;


    @Transactional
    public CategoriaResponseDTO criarCategoria(CategoriaRequestDTO categoriaRequestDTO) {
        Categoria categoria = new Categoria();
        categoria.setNome(categoriaRequestDTO.getNome());
        Categoria categoriaCriada = categoriaRepository.save(categoria);
        return toCategoriaResponseDTO(categoriaCriada);
    }

    @Transactional(readOnly = true)
    public Optional<CategoriaResponseDTO> listarCategoriaPorId(Long id) {
        return categoriaRepository.findById(id).map(this::toCategoriaResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponseDTO> listarTodasCategorias() {
        return categoriaRepository.findAll()
                .stream()
                .map(this::toCategoriaResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<CategoriaResponseDTO> atualizarCategoria(Long id, CategoriaRequestDTO categoriaRequestDTO) {
        return categoriaRepository.findById(id).map(categoriaExistente -> {
            categoriaExistente.setNome(categoriaRequestDTO.getNome());

            Categoria categoriaAtualizada = categoriaRepository.save(categoriaExistente);
            return toCategoriaResponseDTO(categoriaAtualizada);
        });
    }

    @Transactional
    public boolean deletarCategoria(Long id) {
        if (categoriaRepository.existsById(id)) {
            categoriaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public CategoriaResponseDTO toCategoriaResponseDTO(Categoria categoria) {
        CategoriaResponseDTO dto = new CategoriaResponseDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        return dto;
    }
}
