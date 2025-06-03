package br.com.my_wallett.api_wallet.controller;


import br.com.my_wallett.api_wallet.dto.UsuarioRequestDTO;
import br.com.my_wallett.api_wallet.dto.UsuarioResponseDTO;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import br.com.my_wallett.api_wallet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        try {
            UsuarioResponseDTO novoUsuarioDTO = usuarioService.salvarUsuario(usuarioRequestDTO);
            return new ResponseEntity<>(novoUsuarioDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMeuPerfil(@AuthenticationPrincipal UserDetails currentUser) {
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            Optional<UsuarioResponseDTO> usuarioDTO = usuarioService.buscarPorId(usuarioIdLogado);

            return usuarioDTO
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


//    @GetMapping("/{id}")
//    public ResponseEntity<Optional<UsuarioResponseDTO>> buscarUsuarioPorId(@PathVariable Long id){
//        try {
//            Optional<UsuarioResponseDTO> usuarioEncontradoDTO = usuarioService.buscarPorId(id);
//            return new ResponseEntity<>(usuarioEncontradoDTO, HttpStatus.OK);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

//    @GetMapping
//    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodosUsuarios() {
//        try {
//            List<UsuarioResponseDTO> usuariosDTO = usuarioService.listarTodos();
//            return new ResponseEntity<>(usuariosDTO, HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PutMapping({"/{id}"})
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id,
                                              @RequestBody UsuarioRequestDTO usuarioRequestDTO,
                                              @AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);

            if (!id.equals(usuarioIdLogado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Você não tem permissão para atualizar o perfil de outro usuário.");
            }
            Optional<UsuarioResponseDTO> usuarioAtualizadoDTO = usuarioService.atualizarUsuario(id, usuarioRequestDTO);

            return usuarioAtualizadoDTO
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Boolean> deleterUsuario(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);

            if (!id.equals(usuarioIdLogado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            boolean deletado = usuarioService.deletarUsuario(id);
            if (deletado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private Long getUsuarioIdLogado(UserDetails currentUser) {
        if (currentUser == null) {
            throw new SecurityException("Usuário não autenticado. Não é possível obter o ID.");
        }
        String email = currentUser.getUsername();
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado no banco de dados: " + email));
    }

}
