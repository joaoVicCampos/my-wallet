package br.com.my_wallett.api_wallet.controller;


import br.com.my_wallett.api_wallet.dto.UsuarioRequestDTO;
import br.com.my_wallett.api_wallet.dto.UsuarioResponseDTO;
import br.com.my_wallett.api_wallet.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criarUsuario(@RequestBody UsuarioRequestDTO usuarioRequestDTO) {
        try {
            UsuarioResponseDTO novoUsuarioDTO = usuarioService.salvarUsuario(usuarioRequestDTO);
            return new ResponseEntity<>(novoUsuarioDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Optional<UsuarioResponseDTO>> buscarUsuarioPorId(@PathVariable Long id){
        try {
            Optional<UsuarioResponseDTO> usuarioEncontradoDTO = usuarioService.listarUsuarioPorId(id);
            return new ResponseEntity<>(usuarioEncontradoDTO, HttpStatus.OK);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> buscarTodosUsuarios() {
        try {
            List<UsuarioResponseDTO> usuariosDTO = usuarioService.listarTodosUsuario();
            return new ResponseEntity<>(usuariosDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping({"/{id}"})
    public ResponseEntity<Optional<UsuarioResponseDTO>> atualizarUsuario(@PathVariable Long id, @RequestBody UsuarioRequestDTO usuarioRequestDTO){
        try {
            Optional<UsuarioResponseDTO> usuarioAtualizadoDTO = usuarioService.atualizarUsuario(id, usuarioRequestDTO);
            return new ResponseEntity<>(usuarioAtualizadoDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Boolean> deleterUsuario(@PathVariable Long id) {
        try {
            boolean usuarioDeletadoDTO = usuarioService.deletarUsuario(id);
            return new ResponseEntity<>(usuarioDeletadoDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
