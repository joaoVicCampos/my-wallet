package br.com.my_wallett.api_wallet.service;


import br.com.my_wallett.api_wallet.dto.TransacaoRequestDTO;
import br.com.my_wallett.api_wallet.dto.TransacaoResponseDTO;
import br.com.my_wallett.api_wallet.model.Categoria;
import br.com.my_wallett.api_wallet.model.Transacao;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import br.com.my_wallett.api_wallet.repository.CategoriaRepository;
import br.com.my_wallett.api_wallet.repository.TransacaoRepository;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransacaoService {

    @Autowired
    TransacaoRepository transacaoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Transactional
    public TransacaoResponseDTO salvarTransacao(TransacaoRequestDTO transacaoRequestDTO) {
        Usuario usuario = usuarioRepository.findById(transacaoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com Id" + transacaoRequestDTO.getUsuarioId()));

        Categoria categoria = null;
        if (transacaoRequestDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(transacaoRequestDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID" + transacaoRequestDTO.getCategoriaId()));
        }
        Transacao transacao = new Transacao();
        transacao.setDescricao(transacaoRequestDTO.getDescricao());
        transacao.setValor(transacaoRequestDTO.getValor());
        transacao.setDataInicio(transacaoRequestDTO.getDataInicio());
        transacao.setDataFim(transacaoRequestDTO.getDataFim());
        transacao.setTipo(transacaoRequestDTO.getTipo());
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

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
    public Optional<TransacaoResponseDTO> atualizarTransacao(Long transacaoId, TransacaoRequestDTO transacaoRequestDTO){

        Optional<Transacao> transacaoOptional = transacaoRepository.findById(transacaoId);

        if (transacaoOptional.isEmpty()) {
            return Optional.empty();
        }
        Transacao transacaoExistente = transacaoOptional.get();

        Usuario usuario = usuarioRepository.findById(transacaoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com ID" + transacaoRequestDTO.getUsuarioId()));

        Categoria categoria = null;
        if (transacaoRequestDTO.getCategoriaId() != null) {
            categoria = categoriaRepository.findById(transacaoRequestDTO.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria não encontrada com ID" + transacaoRequestDTO.getCategoriaId()));
        }
            transacaoExistente.setValor(transacaoRequestDTO.getValor());
            transacaoExistente.setDescricao(transacaoRequestDTO.getDescricao());
            transacaoExistente.setTipo(transacaoRequestDTO.getTipo());
            transacaoExistente.setUsuario(usuario);
            transacaoExistente.setCategoria(categoria);

            Transacao transacaoAtualizada = transacaoRepository.save(transacaoExistente);
            return Optional.of(toTransacaoResponseDTO(transacaoAtualizada));
    }

    @Transactional
    public boolean  deletarTransacao(Long id) {
        if (transacaoRepository.existsById(id)) {
            transacaoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public List<TransacaoResponseDTO> buscarTransacaoPeloUsuario(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            return List.of();
        }
        List<Transacao> transacoesPorUsuario = transacaoRepository.findByUsuarioId(usuarioId);
        return transacoesPorUsuario
                .stream()
                .map(this::toTransacaoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<TransacaoResponseDTO> buscarTransacaoPorCategoria(Long categoriaId) {
        if (!categoriaRepository.existsById(categoriaId)) {
            return List.of();
        }
        List<Transacao> transacoesPorCategoria = transacaoRepository.findByCategoriaId(categoriaId);
        return transacoesPorCategoria
                .stream()
                .map(this::toTransacaoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> buscaComFiltros(Long usuarioId, Long categoriaId, LocalDate dataInicio, LocalDate dataFim, TipoTransacao tipo){

        String dataIninioStr = null;
        if (dataInicio != null){
            dataIninioStr = dataIninioStr.format(String.valueOf(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        String dataFimStr = null;
        if (dataFim != null){
            dataFimStr = dataIninioStr.format(String.valueOf((DateTimeFormatter.ISO_LOCAL_DATE)));
        }

        List<Transacao> transacoesFiltradas = transacaoRepository.findComFiltros(usuarioId, categoriaId, dataInicio, dataFim, tipo);
        return transacoesFiltradas
                .stream()
                .map(this::toTransacaoResponseDTO)
                .collect(Collectors.toList());
    }

    private TransacaoResponseDTO toTransacaoResponseDTO(Transacao transacao) {
        TransacaoResponseDTO dto = new TransacaoResponseDTO();
        dto.setId(transacao.getId());
        dto.setDescricao(transacao.getDescricao());
        dto.setValor(transacao.getValor());
        dto.setDataInicio(transacao.getDataInicio());
        dto.setDataFim(transacao.getDataFim());
        dto.setTipo(transacao.getTipo());
        if (transacao.getUsuario() != null) {
            dto.setUsuarioId(transacao.getUsuario().getId());
            dto.setNomeUsuario(transacao.getUsuario().getNome());
        }
        if (transacao.getCategoria() != null) {
            dto.setCategoriaId(transacao.getCategoria().getId());
            dto.setCategoriaNome(transacao.getCategoria().getNome());
        }
        return dto;
    }
}
