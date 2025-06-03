package br.com.my_wallett.api_wallet.controller;

import br.com.my_wallett.api_wallet.dto.ContribuicaoObjetivoRequestDTO;
import br.com.my_wallett.api_wallet.dto.ObjetivoRequestDTO;
import br.com.my_wallett.api_wallet.dto.ObjetivoResponseDTO;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import br.com.my_wallett.api_wallet.service.ObjetivoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/objetivos")
@CrossOrigin(origins = "*")
public class ObjetivoController {

    @Autowired
    ObjetivoService objetivoService;

    @Autowired
    UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> criarObjetivo(@RequestBody ObjetivoRequestDTO objetivoRequestDTO,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autenticado.");
        }
        String emailUsuarioLogado = userDetails.getUsername();
        Optional<Usuario>usuarioOptional = usuarioRepository.findByEmail(emailUsuarioLogado);
        if (usuarioOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Usuário logado não encontrado.");
        }

        Long usuarioIdLogado = usuarioOptional.get().getId();
        if (objetivoRequestDTO.getUsuarioId() != null && !objetivoRequestDTO.getUsuarioId().equals(usuarioIdLogado)){
            System.out.println("Aviso: usuarioId no DTO ("+objetivoRequestDTO.getUsuarioId()+") diferente do usuário logado ("+usuarioIdLogado+"). Usando ID do usuário logado.");
        }
        objetivoRequestDTO.setUsuarioId(usuarioIdLogado);

        try {
            ObjetivoResponseDTO novoObjetivo = objetivoService.criarObjetivo(objetivoRequestDTO);
            return new ResponseEntity<>(novoObjetivo, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarObjetivoPorId(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String emailUsuarioLogado = userDetails.getUsername();

        Optional<ObjetivoResponseDTO> objetivoDTO = objetivoService.buscarObjetivoPorId(id);

        if(objetivoDTO.isPresent()){
            if (!objetivoDTO.get().getUsuarioId().equals(getUsuarioIdLogado(emailUsuarioLogado))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para visualizar este objetivo.");
            }
            return new ResponseEntity<>(objetivoDTO.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<?> listarMeusObjetivos(@AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não autorizado");
        }
        String emailUsuarioLogado = userDetails.getUsername();

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(emailUsuarioLogado);
        if (usuarioOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Usuário logado não encontrado no banco de dados.");
        }
        Long usuarioIdLogdo =  usuarioOptional.get().getId();

        List<ObjetivoResponseDTO> objetivos = objetivoService.buscarPorUsuarioId(usuarioIdLogdo);
        return new ResponseEntity<>(objetivos, HttpStatus.OK);
    }

//    @GetMapping("/usuario/{usuarioId}")
//    public ResponseEntity<List<ObjetivoResponseDTO>> buscarObjetivoPorUsuario(@PathVariable Long usuarioId) {
//        try {
//            List<ObjetivoResponseDTO> objetivosPorUsuarios = objetivoService.buscarPorUsuarioId(usuarioId);
//            return new ResponseEntity<>(objetivosPorUsuarios, HttpStatus.OK);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarObjetivo(@PathVariable Long id,
                                                    @RequestBody ObjetivoRequestDTO objetivoRequestDTO,
                                                    @AuthenticationPrincipal UserDetails userDetails
                                                                           ){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String emailUsuarioLogado = userDetails.getUsername();
        Optional<ObjetivoResponseDTO> objetivoExistente = objetivoService.buscarObjetivoPorId(id);
        if (objetivoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Objetivo não encontrado com ID: " + id);
        }
        if (!objetivoExistente.get().getUsuarioId().equals(getUsuarioIdLogado(emailUsuarioLogado))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para alterar este objetivo.");
        }

        if (objetivoRequestDTO.getUsuarioId() != null && !objetivoRequestDTO.getUsuarioId().equals(objetivoExistente.get().getUsuarioId())) {
            return ResponseEntity.badRequest().body("Não é permitido alterar o proprietário do objetivo desta forma.");
        }
        objetivoRequestDTO.setUsuarioId(objetivoExistente.get().getUsuarioId());

        try {
            ObjetivoResponseDTO novoObjetivo = objetivoService.criarObjetivo(objetivoRequestDTO);
            return new ResponseEntity<>(novoObjetivo, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build(); // Sem corpo de String
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletarObjetivo(@PathVariable Long id,
                                                  @AuthenticationPrincipal UserDetails userDetails
                                                   ){
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String emailUsuarioLogado = userDetails.getUsername();

        Optional<ObjetivoResponseDTO> objetivoExistente = objetivoService.buscarObjetivoPorId(id);
        if (objetivoExistente.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Objetivo não encontrado com ID: " + id);
        }
        if (!objetivoExistente.get().getUsuarioId().equals(getUsuarioIdLogado(emailUsuarioLogado))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você não tem permissão para deletar este objetivo.");
        }

        boolean deletado = objetivoService.deletarObjetivo(id);
        if (deletado) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao deletar o objetivo.");
        }
    }

    private Long getUsuarioIdLogado(String email) {
        return usuarioRepository.findByEmail(email)
                .map(Usuario::getId)
                .orElseThrow(() -> new RuntimeException("Usuário logado não encontrado no banco de dados."));
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
