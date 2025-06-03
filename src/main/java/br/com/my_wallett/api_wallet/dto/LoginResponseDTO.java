package br.com.my_wallett.api_wallet.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tipoToken = "Bearer";
//    private Long usuarioId;
//    private String nomeUsuario;
}
