package br.com.my_wallett.api_wallet.controller;

import br.com.my_wallett.api_wallet.dto.CategoriaRequestDTO;
import br.com.my_wallett.api_wallet.dto.CategoriaResponseDTO;
import br.com.my_wallett.api_wallet.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias")
@CrossOrigin(origins = "*")
public class CategoriaController {

    @Autowired
    CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDTO> criarCategoria(@RequestBody CategoriaRequestDTO categoriaRequestDTO) {
        try {
            CategoriaResponseDTO novaCategoriaDTO = categoriaService.criarCategoria(categoriaRequestDTO);
            return new ResponseEntity<>(novaCategoriaDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDTO>> buscarTodasCategorias() {
        try {
            List<CategoriaResponseDTO> categoriasDTO = categoriaService.listarTodasCategorias();
            return new ResponseEntity<>(categoriasDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<CategoriaResponseDTO>> buscarCategoriaPorId(@PathVariable Long id) {
        try {
            Optional<CategoriaResponseDTO> categoriaEncontradaDTO = categoriaService.listarCategoriaPorId(id);
            return new ResponseEntity<>(categoriaEncontradaDTO, HttpStatus.OK);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Optional<CategoriaResponseDTO>> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriaRequestDTO categoriaRequestDTO) {
        try {
            Optional<CategoriaResponseDTO> categoriaAtualizadaDTO = categoriaService.atualizarCategoria(id, categoriaRequestDTO);
            return new ResponseEntity<>(categoriaAtualizadaDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deletarCategoria(@PathVariable Long id) {
        try {
            boolean categoriaDeletadaDTO = categoriaService.deletarCategoria(id);
            return new ResponseEntity<>(categoriaDeletadaDTO, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
