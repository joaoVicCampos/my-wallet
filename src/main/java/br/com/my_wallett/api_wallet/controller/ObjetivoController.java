package br.com.my_wallett.api_wallet.controller;

import br.com.my_wallett.api_wallet.dto.ContribuicaoObjetivoRequestDTO;
import br.com.my_wallett.api_wallet.dto.ObjetivoRequestDTO;
import br.com.my_wallett.api_wallet.dto.ObjetivoResponseDTO;
import br.com.my_wallett.api_wallet.service.ObjetivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/objetivos")
@CrossOrigin(origins = "*")
public class ObjetivoController {

    @Autowired
    ObjetivoService objetivoService;

    @PostMapping
    public ResponseEntity<ObjetivoResponseDTO> criarObjetivo(@RequestBody ObjetivoRequestDTO objetivoRequestDTO){
        try {
            ObjetivoResponseDTO novoObjetivo = objetivoService.criarObjetivo(objetivoRequestDTO);
            return new ResponseEntity<>(novoObjetivo, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<ObjetivoResponseDTO>> buscarObjetivoPorId(@PathVariable Long id){
        try {
            Optional<ObjetivoResponseDTO> objtivoEncontrado = objetivoService.buscarObjetivoPorId(id);
            return new ResponseEntity<>(objtivoEncontrado, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<ObjetivoResponseDTO>> buscarTodosObjetivos(){
        try {
            List<ObjetivoResponseDTO> objetivos = objetivoService.buscarTodosObjetivos();
            return new ResponseEntity<>(objetivos, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ObjetivoResponseDTO>> buscarObjetivoPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<ObjetivoResponseDTO> objetivosPorUsuarios = objetivoService.buscarPorUsuarioId(usuarioId);
            return new ResponseEntity<>(objetivosPorUsuarios, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<ObjetivoResponseDTO>> atualizarObjetivo(@PathVariable Long id, @RequestBody ObjetivoRequestDTO objetivoRequestDTO){
        try {
            Optional<ObjetivoResponseDTO> objetivoAtualizado = objetivoService.atualizarObjetivo(id, objetivoRequestDTO);
            return new ResponseEntity<>(objetivoAtualizado, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarObjetivo(@PathVariable Long id){
        try {
            boolean objetivoDeletado = objetivoService.deletarObjetivo(id);
            return new ResponseEntity<>(objetivoDeletado, HttpStatus.OK);
        } catch (Exception e) {
            return  ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{objetivoId}/contribuir")
    public ResponseEntity<?> contribuirParaObjetivo(@PathVariable Long objetivoId, @RequestBody ContribuicaoObjetivoRequestDTO contribuicaoObjetivoRequestDTO){
        try {
            if (contribuicaoObjetivoRequestDTO.getValorContribuicao() == null) {
                return ResponseEntity.badRequest().build();
            }
            ObjetivoResponseDTO objetivoAtualizado = objetivoService.contribuirComObjetivo(objetivoId, contribuicaoObjetivoRequestDTO.getValorContribuicao());
            return new ResponseEntity<>(objetivoAtualizado, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
