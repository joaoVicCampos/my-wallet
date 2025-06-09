package br.com.my_wallett.api_wallet.controller; // Verifique o nome do seu pacote

import br.com.my_wallett.api_wallet.dto.ResumoMensalDTO;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import br.com.my_wallett.api_wallet.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/resumo-mensal")
    public ResponseEntity<?> getResumoMensal(
            @RequestParam int ano,
            @RequestParam int mes,
            @AuthenticationPrincipal UserDetails currentUser) {

        try {
            String emailUsuarioLogado = currentUser.getUsername();
            Long usuarioIdLogado = usuarioRepository.findByEmail(emailUsuarioLogado)
                    .map(Usuario::getId)
                    .orElseThrow(() -> new RuntimeException("Usuário autenticado não encontrado no banco de dados."));

            ResumoMensalDTO resumo = relatorioService.getResumoMensal(usuarioIdLogado, ano, mes);

            return ResponseEntity.ok(resumo);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao gerar relatório: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");
        }
    }
}