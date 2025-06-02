package br.com.my_wallett.api_wallet.controller;


import br.com.my_wallett.api_wallet.dto.OrcamentoRequestoDTO;
import br.com.my_wallett.api_wallet.dto.OrcamentoResponseDTO;
import br.com.my_wallett.api_wallet.model.Orcamento;
import br.com.my_wallett.api_wallet.service.OrcamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orcamentos")
@CrossOrigin("*")
public class OrcamentoController {

    @Autowired
    OrcamentoService orcamentoService;

    @PostMapping
    public ResponseEntity<OrcamentoResponseDTO> criarOrcamento(@RequestBody OrcamentoRequestoDTO orcamentoRequestoDTO) {
        try {
            OrcamentoResponseDTO orcamentoNovo = orcamentoService.criarOrcamento(orcamentoRequestoDTO);
            return new ResponseEntity<>(orcamentoNovo, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrcamentoResponseDTO>> buscarOrcamentos(){
        try {
            List<OrcamentoResponseDTO> orcamentos = orcamentoService.buscarOrcamentos();
            return new ResponseEntity<>(orcamentos, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<OrcamentoResponseDTO>> buscOrcamentoPorId(@PathVariable Long id){
        try {
            Optional<OrcamentoResponseDTO> orcamentoEncontrado = orcamentoService.buscarOrcamentoPorId(id);
            return new ResponseEntity<>(orcamentoEncontrado, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrcamentoResponseDTO>> buscarOrcamentoPorUsuario(@PathVariable Long usuairioId){
        try {
            List<OrcamentoResponseDTO> orcamentoPorUsuario = orcamentoService.buscarPorUsuarioId(usuairioId);
            return new ResponseEntity<>(orcamentoPorUsuario, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/usuario/{usuarioId}/period")
    public ResponseEntity<List<OrcamentoResponseDTO>> buscarPorUsuarioPeriodo(@PathVariable Long usuarioId, int mes, int ano){
        try {
            List<OrcamentoResponseDTO> orcamentoPorUsuarioPeriodo = orcamentoService.buscarPorUsuarioMesEAno(usuarioId, mes, ano);
            return new ResponseEntity<>(orcamentoPorUsuarioPeriodo, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    private ResponseEntity<Optional<OrcamentoResponseDTO>> atualizarOrcamento(@PathVariable Long id, @RequestBody OrcamentoRequestoDTO orcamentoRequestoDTO){
        try {
            Optional<OrcamentoResponseDTO> orcamentoAtualizado = orcamentoService.atualizarOrcamento(id, orcamentoRequestoDTO);
            return new ResponseEntity<>(orcamentoAtualizado,HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{id}")
    private ResponseEntity<Boolean> deletarOrcamento(@PathVariable Long id){
        try {
            Boolean orcamentoDeletado = orcamentoService.deletarOrcamento(id);
            return new ResponseEntity<>(orcamentoDeletado, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
