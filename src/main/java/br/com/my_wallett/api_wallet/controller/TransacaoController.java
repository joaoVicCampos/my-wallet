package br.com.my_wallett.api_wallet.controller;


import br.com.my_wallett.api_wallet.dto.TransacaoRequestDTO;
import br.com.my_wallett.api_wallet.dto.TransacaoResponseDTO;
import br.com.my_wallett.api_wallet.service.TransacaoService;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    @PostMapping
    public ResponseEntity<TransacaoResponseDTO> criarTransacao(@RequestBody TransacaoRequestDTO transacaoRequestDTO) {
        try {
            TransacaoResponseDTO novaTransacao = transacaoService.salvarTransacao(transacaoRequestDTO);
            return new ResponseEntity<>(novaTransacao, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Optional<TransacaoResponseDTO>> buscarTransacaoPorId(@PathVariable Long id) {
        try {
            Optional<TransacaoResponseDTO> transacaoEncontradaDTO = transacaoService.listarTransacaoPorId(id);
            return new ResponseEntity<>(transacaoEncontradaDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<TransacaoResponseDTO>> atualizarTransacao(@PathVariable Long id, @RequestBody TransacaoRequestDTO transacaoRequestDTO) {
        try {
            Optional<TransacaoResponseDTO> trasacaoAtualizadaDTO = transacaoService.atualizarTransacao(id, transacaoRequestDTO);
            return new ResponseEntity<>(trasacaoAtualizadaDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarTransacao(@PathVariable Long id) {
        try {
            boolean transacaoDeletada = transacaoService.deletarTransacao(id);
            return new ResponseEntity<>(transacaoDeletada, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
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

    @GetMapping
    public ResponseEntity<List<TransacaoResponseDTO>> listarTransacoesComFiltros(
            @RequestParam(required = false) Long usuarioId,
            @RequestParam(required = false) Long categoriaId
    ) {
        try {
            List<TransacaoResponseDTO> transacoesComFiltro = transacaoService.buscaComFiltros(usuarioId, categoriaId);
            return new ResponseEntity<>(transacoesComFiltro, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
