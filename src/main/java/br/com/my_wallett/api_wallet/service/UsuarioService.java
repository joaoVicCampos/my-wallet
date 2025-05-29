package br.com.my_wallett.api_wallet.service;


import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.UsuarioReposiroty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioReposiroty usuarioReposiroty;

    public Usuario salvarUsuario(Usuario usuario) {
        // verificar email, criptografar senha
        return usuarioReposiroty.save(usuario);
    }

    public Optional<Usuario> listarUsuarioPorId(Long id) {
        return usuarioReposiroty.findById(id);
    }

    public List<Usuario> listarTodosUsuario() {
        return usuarioReposiroty.findAll();
    }
}


