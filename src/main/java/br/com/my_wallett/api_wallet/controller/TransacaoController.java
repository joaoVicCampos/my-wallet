package br.com.my_wallett.api_wallet.controller;


import br.com.my_wallett.api_wallet.dto.TransacaoRequestDTO;
import br.com.my_wallett.api_wallet.dto.TransacaoResponseDTO;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import br.com.my_wallett.api_wallet.service.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criarTransacao(@RequestBody TransacaoRequestDTO transacaoRequestDTO,
                                            @AuthenticationPrincipal UserDetails currentUser
                                            ) {
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            transacaoRequestDTO.setUsuarioId(usuarioIdLogado);

            TransacaoResponseDTO novaTransacao = transacaoService.salvarTransacao(transacaoRequestDTO);
            return new ResponseEntity<>(novaTransacao, HttpStatus.CREATED);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao criar transação.");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<TransacaoResponseDTO> buscarTransacaoPorId(@PathVariable Long id,
                                                                     @AuthenticationPrincipal UserDetails currentUser
                                                                               ){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            Optional<TransacaoResponseDTO> transacaoEncontradaDTO = transacaoService.buscarTransacaoPorIdEUsuario(id, usuarioIdLogado);
            return transacaoEncontradaDTO
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarTransacao(@PathVariable Long id,
                                                @RequestBody TransacaoRequestDTO transacaoRequestDTO,
                                                @AuthenticationPrincipal UserDetails currentUser){
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            Optional<TransacaoResponseDTO> transacaoAtualizadaDTO =
                    transacaoService.atualizarTransacao(id, transacaoRequestDTO, usuarioIdLogado);

            return transacaoAtualizadaDTO
                    .map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage() != null && (e.getMessage().contains("não encontrado com ID") || e.getMessage().contains("não encontrada com ID"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar transação.");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarTransacao(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails currentUser) {
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            boolean deletado = transacaoService.deletarTransacao(id, usuarioIdLogado);

            if (deletado) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (RuntimeException e) {
            e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<TransacaoResponseDTO>> buscarTransacaoPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<TransacaoResponseDTO> transacoesPorUsariosDTO = transacaoService.buscarTransacaoPeloUsuario(usuarioId);
            if (transacoesPorUsariosDTO.isEmpty()) {
                return new ResponseEntity<>(transacoesPorUsariosDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(transacoesPorUsariosDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<TransacaoResponseDTO>> buscarTransacaoPorCategoria(@PathVariable Long categoriaId) {
        try {
            List<TransacaoResponseDTO> transacoesPorCategoriaDTO = transacaoService.buscarTransacaoPorCategoria(categoriaId);
            if (transacoesPorCategoriaDTO.isEmpty()) {
                return new ResponseEntity<>(transacoesPorCategoriaDTO, HttpStatus.OK);
            }
            return new ResponseEntity<>(transacoesPorCategoriaDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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

    @GetMapping
    public ResponseEntity<?> listarTransacoesComFiltros(
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false)TipoTransacao tipo,
            @AuthenticationPrincipal UserDetails currentUser
            ) {
        try {
            Long usuarioIdLogado = getUsuarioIdLogado(currentUser);
            List<TransacaoResponseDTO> transacoesComFiltro = transacaoService.buscaComFiltros(
                    usuarioIdLogado,
                    categoriaId,
                    dataInicio,
                    dataFim,
                    tipo
            );
            return new ResponseEntity<>(transacoesComFiltro, HttpStatus.OK);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao listar transações.");
        }
    }
}
