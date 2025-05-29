package br.com.my_wallett.api_wallet.service;


import br.com.my_wallett.api_wallet.dto.TransacaoRequestDTO;
import br.com.my_wallett.api_wallet.dto.TransacaoResponseDTO;
import br.com.my_wallett.api_wallet.model.Transacao;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.TransacaoRepository;
import br.com.my_wallett.api_wallet.repository.UsuarioReposiroty;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    TransacaoRepository transacaoRepository;

    @Autowired
    UsuarioReposiroty usuarioReposiroty;

    @Transactional
    public TransacaoResponseDTO salvarTransacao(TransacaoRequestDTO transacaoRequestDTO) {
        Usuario usuario = usuarioReposiroty.findById(transacaoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com Id" + transacaoRequestDTO.getUsuarioId()));
        Transacao transacao = new Transacao();
        transacao.setDescricao(transacaoRequestDTO.getDescricao());
        transacao.setValor(transacaoRequestDTO.getValor());
        transacao.setData(transacaoRequestDTO.getData());
        transacao.setTipo(transacaoRequestDTO.getTipo());
        transacao.setUsuario(usuario);

        Transacao transacaoSalva = transacaoRepository.save(transacao);

        return toTransacaoResponseDTO(transacaoSalva);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarTodasTransacoes() {
        return transacaoRepository.findAll()
                .stream()
                .map(this::toTransacaoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<TransacaoResponseDTO> listarTransacaoPorId(Long id) {
        return transacaoRepository.findById(id).map(this::toTransacaoResponseDTO);
    }

    @Transactional
    public Optional<TransacaoResponseDTO> atualizarTransacao(Long id, TransacaoRequestDTO transacaoRequestDTO){
        return transacaoRepository.findById(id).map(transacaoExistente -> {
            Usuario usuario = usuarioReposiroty.findById(transacaoRequestDTO.getUsuarioId())
                            .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID" + transacaoRequestDTO.getUsuarioId()));
            transacaoExistente.setData(transacaoRequestDTO.getData());
            transacaoExistente.setValor(transacaoRequestDTO.getValor());
            transacaoExistente.setDescricao(transacaoRequestDTO.getDescricao());
            transacaoExistente.setTipo(transacaoRequestDTO.getTipo());
            transacaoExistente.setUsuario(usuario);

            Transacao transacaoAtualizada = transacaoRepository.save(transacaoExistente);
            return toTransacaoResponseDTO(transacaoAtualizada);
        });
    }

    @Transactional
    public boolean  deletarTransacao(Long id) {
        if (transacaoRepository.existsById(id)) {
            transacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private TransacaoResponseDTO toTransacaoResponseDTO(Transacao transacao) {
        TransacaoResponseDTO dto = new TransacaoResponseDTO();
        dto.setId(transacao.getId());
        dto.setDescricao(transacao.getDescricao());
        dto.setValor(transacao.getValor());
        dto.setData(transacao.getData());
        dto.setTipo(transacao.getTipo());
        if (transacao.getUsuario() != null) {
            dto.setUsuarioId(transacao.getUsuario().getId());
            dto.setNomeUsuario(transacao.getUsuario().getNome());
        }
        return dto;
    }
}
