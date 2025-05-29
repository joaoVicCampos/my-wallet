package br.com.my_wallett.api_wallet.service;


import br.com.my_wallett.api_wallet.dto.UsuarioRequestDTO;
import br.com.my_wallett.api_wallet.dto.UsuarioResponseDTO;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioReposiroty;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioReposiroty usuarioReposiroty;

    @Transactional
    public UsuarioResponseDTO salvarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        // verificar email, criptografar senha
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        // senha deve ser criptografada, antes de subir para produção
        usuario.setSenha(usuarioRequestDTO.getSenha());
        Usuario usuarioSalvo = usuarioReposiroty.save(usuario);
        return toUsuarioResponseDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> listarUsuarioPorId(Long id) {
        return usuarioReposiroty.findById(id).map(this::toUsuarioResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodosUsuario() {
        return usuarioReposiroty.findAll()
                .stream() // --> Transforma em fluxo de processamento
                .map(this::toUsuarioResponseDTO)// --> mapeia cada Usuario para usar UsuarioResponseDTO
                .collect(Collectors.toList()); // --> Coleta os resultados de volta em uma lista
    }

    @Transactional
    public Optional<UsuarioResponseDTO> atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioReposiroty.findById(id).map(usuarioExistente -> {
            usuarioExistente.setNome(usuarioRequestDTO.getNome());
            usuarioExistente.setEmail(usuarioRequestDTO.getEmail());

            if (usuarioRequestDTO.getSenha() != null && !usuarioRequestDTO.getSenha().isEmpty()) {
                usuarioExistente.setSenha(usuarioRequestDTO.getSenha());
            }

            Usuario usuarioAtualizado = usuarioReposiroty.save(usuarioExistente);
            return toUsuarioResponseDTO(usuarioAtualizado);
        });
    }

    @Transactional
    public boolean deletarUsuario(Long id) {
        if (usuarioReposiroty.existsById(id)) {
            usuarioReposiroty.deleteById(id);
            return true;
        }
        return false;
    }


    private UsuarioResponseDTO toUsuarioResponseDTO(Usuario usuario) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        return dto;
    }
}


