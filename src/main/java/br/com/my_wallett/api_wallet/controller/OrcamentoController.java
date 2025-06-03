package br.com.my_wallett.api_wallet.controller;


import br.com.my_wallett.api_wallet.dto.OrcamentoRequestoDTO;
import br.com.my_wallett.api_wallet.dto.OrcamentoResponseDTO;
import br.com.my_wallett.api_wallet.model.Orcamento;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import br.com.my_wallett.api_wallet.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orcamentos")
@CrossOrigin("*")
public class OrcamentoController {

    @Autowired
    OrcamentoService orcamentoService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criarOrcamento(@RequestBody OrcamentoRequestoDTO orcamentoRequestoDTO,
                                            @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            orcamentoRequestoDTO.setUsuarioId(usuarioIdLogado);

            OrcamentoResponseDTO novoOrcamento = orcamentoService.criarOrcamento(orcamentoRequestoDTO);
            return new ResponseEntity<>(novoOrcamento, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> buscOrcamentoPorId(@PathVariable Long id,
                                                @AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            Optional<OrcamentoResponseDTO> orcamentoDTO = orcamentoService.buscarOrcamentoPorIdEUsuario(id, usuarioIdLogado);
            return orcamentoDTO
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> listarOrcamentosDoUsuarioLogado(@AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            List<OrcamentoResponseDTO> orcamentos = orcamentoService.buscarPorUsuarioId(usuarioIdLogado);
            return new ResponseEntity<>(orcamentos, HttpStatus.OK);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

    }

    @GetMapping("/periodo")
    public ResponseEntity<?> listarOrcamentosPorUsuarioEMesAno(@RequestParam int mes,
                                                               @RequestParam int ano,
                                                               @AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            List<OrcamentoResponseDTO> orcamentos = orcamentoService.buscarPorUsuarioMesEAno(usuarioIdLogado, mes, ano);
            return new ResponseEntity<>(orcamentos, HttpStatus.OK);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<?> atualizarOrcamento(@PathVariable Long id,
                                                 @RequestBody OrcamentoRequestoDTO orcamentoRequestoDTO,
                                                 @AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            orcamentoRequestoDTO.setUsuarioId(usuarioIdLogado);

            Optional<OrcamentoResponseDTO> orcamentoAtualizado = orcamentoService.atualizarOrcamento(id, orcamentoRequestoDTO, usuarioIdLogado);
            return orcamentoAtualizado
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Boolean> deletarOrcamento(@PathVariable Long id,
                                                     @AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            boolean deletado = orcamentoService.deletarOrcamento(id, usuarioIdLogado);
            if (deletado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private Long getUsuarioIdLogado(UserDetails userDetails) {
        if (userDetails == null) {
            throw new SecurityException("Usuário não autenticado.");
        }
        String emailUsuarioLogado = userDetails.getUsername();
        return usuarioRepository.findByEmail(emailUsuarioLogado)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado no banco de dados: " + emailUsuarioLogado));
    }

}
