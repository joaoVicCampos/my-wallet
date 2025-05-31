package br.com.my_wallett.api_wallet.service;


import br.com.my_wallett.api_wallet.dto.UsuarioRequestDTO;
import br.com.my_wallett.api_wallet.dto.UsuarioResponseDTO;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO salvarUsuario(UsuarioRequestDTO usuarioRequestDTO) {
        // verificar email, criptografar senha
        Usuario usuario = new Usuario();
        usuario.setNome(usuarioRequestDTO.getNome());
        usuario.setEmail(usuarioRequestDTO.getEmail());
        usuario.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return toUsuarioResponseDTO(usuarioSalvo);
    }

    @Transactional(readOnly = true)
    public Optional<UsuarioResponseDTO> buscarPorId(Long id) {
        return usuarioRepository.findById(id).map(this::toUsuarioResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream() // --> Transforma em fluxo de processamento
                .map(this::toUsuarioResponseDTO)// --> mapeia cada Usuario para usar UsuarioResponseDTO
                .collect(Collectors.toList()); // --> Coleta os resultados de volta em uma lista
    }

    @Transactional
    public Optional<UsuarioResponseDTO> atualizarUsuario(Long id, UsuarioRequestDTO usuarioRequestDTO) {
        return usuarioRepository.findById(id).map(usuarioExistente -> {
            usuarioExistente.setNome(usuarioRequestDTO.getNome());
            usuarioExistente.setEmail(usuarioRequestDTO.getEmail());

            if (usuarioRequestDTO.getSenha() != null && !usuarioRequestDTO.getSenha().isEmpty()) {
                usuarioExistente.setSenha(passwordEncoder.encode(usuarioRequestDTO.getSenha()));
            }

            Usuario usuarioAtualizado = usuarioRepository.save(usuarioExistente);
            return toUsuarioResponseDTO(usuarioAtualizado);
        });
    }

    @Transactional
    public boolean deletarUsuario(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
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


