package br.com.my_wallett.api_wallet.service;


import br.com.my_wallett.api_wallet.dto.ObjetivoRequestDTO;
import br.com.my_wallett.api_wallet.dto.ObjetivoResponseDTO;
import br.com.my_wallett.api_wallet.dto.TransacaoRequestDTO;
import br.com.my_wallett.api_wallet.model.Objetivo;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.model.enums.StatusObjetivo;
import br.com.my_wallett.api_wallet.model.enums.TipoTransacao;
import br.com.my_wallett.api_wallet.repository.ObjetivoRepository;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ObjetivoService {

    @Autowired
    ObjetivoRepository objetivoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TransacaoService transacaoService;

    @Transactional
    public ObjetivoResponseDTO criarObjetivo(ObjetivoRequestDTO objetivoRequestDTO){
        Usuario usuario = usuarioRepository.findById(objetivoRequestDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com Id" + objetivoRequestDTO.getUsuarioId()));

        Objetivo objetivo = new Objetivo();
        objetivo.setNome(objetivoRequestDTO.getNome());
        objetivo.setValorMeta(objetivoRequestDTO.getValorMeta());
        objetivo.setDataConclusaoPrevista(objetivoRequestDTO.getDataConclusaoPrevista());
        objetivo.setValorAtual(BigDecimal.ZERO);
        objetivo.setDataCriacao(LocalDate.now());
        objetivo.setStatus(StatusObjetivo.PENDENTE);
        objetivo.setUsuario(usuario);

        Objetivo objetivoSalvo = objetivoRepository.save(objetivo);

        return toObjetivoResponseDTO(objetivoSalvo);
    }


    @Transactional(readOnly = true)
    public Optional<ObjetivoResponseDTO> buscarObjetivoPorId(Long id){
        return objetivoRepository.findById(id).map(this::toObjetivoResponseDTO);
    }


    @Transactional(readOnly = true)
    public List<ObjetivoResponseDTO> buscarTodosObjetivos(){
        return objetivoRepository.findAll()
                .stream()
                .map(this::toObjetivoResponseDTO)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<ObjetivoResponseDTO> buscarPorUsuarioId(Long usuarioId){
        if (!usuarioRepository.existsById(usuarioId)){
            List.of();
        }
        List<Objetivo> objetivosPorUsuario = objetivoRepository.findByUsuarioId(usuarioId);
        return objetivosPorUsuario
                .stream()
                .map(this::toObjetivoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ObjetivoResponseDTO> atualizarObjetivo(Long id, ObjetivoRequestDTO requestDTO) {
        return objetivoRepository.findById(id)
                .map(objetivoExistente -> {
                    objetivoExistente.setNome(requestDTO.getNome());
                    objetivoExistente.setValorMeta(requestDTO.getValorMeta());
                    objetivoExistente.setDataConclusaoPrevista(requestDTO.getDataConclusaoPrevista());

                    Objetivo objetivoAtualizado = objetivoRepository.save(objetivoExistente);
                    return toObjetivoResponseDTO(objetivoAtualizado);
                });
    }

    @Transactional
    public boolean deletarObjetivo(Long id)     {
        if (objetivoRepository.existsById(id)) {
            objetivoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public ObjetivoResponseDTO contribuirComObjetivo(Long objetivoId, BigDecimal valorContribuicao) {
        if (valorContribuicao == null || valorContribuicao.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da conribuição deve ser maior que zero");
        }
        Objetivo objetivo = objetivoRepository.findById(objetivoId)
                .orElseThrow(() ->  new RuntimeException("Objetivo não encontrado"));

        if (objetivo.getStatus() == StatusObjetivo.CONCLUIDO) {
            throw new IllegalArgumentException("Objetivo já concluído");
        }

        if (objetivo.getStatus() == StatusObjetivo.CANCELADO) {
            throw new IllegalArgumentException("Objetivo cancela");
        }

        BigDecimal novoValorAtual = objetivo.getValorAtual().add(valorContribuicao);
        objetivo.setValorAtual(novoValorAtual);

        StatusObjetivo statusAnterior = objetivo.getStatus();
        if (objetivo.getValorAtual().compareTo(objetivo.getValorMeta()) >= 0){
            objetivo.setStatus(StatusObjetivo.CONCLUIDO);
        } else if (objetivo.getStatus() == StatusObjetivo.PENDENTE && objetivo.getValorAtual().compareTo(BigDecimal.ZERO) > 0) {
            objetivo.setStatus(StatusObjetivo.EM_ANDAMENTO);
        }

        Objetivo objetivoAtualizado = objetivoRepository.save(objetivo);

        TransacaoRequestDTO transacaoContribuicaoDTO = new TransacaoRequestDTO();
        transacaoContribuicaoDTO.setDescricao("Contribuição para objetivo: " + objetivoAtualizado.getNome());
        transacaoContribuicaoDTO.setValor(valorContribuicao);
        transacaoContribuicaoDTO.setDataInicio(LocalDate.now());
        transacaoContribuicaoDTO.setDataFim(LocalDate.now());
        transacaoContribuicaoDTO.setTipo(TipoTransacao.DESPESA);
        transacaoContribuicaoDTO.setUsuarioId(objetivoAtualizado.getUsuario().getId());

        transacaoContribuicaoDTO.setCategoriaId(null);

        try {
            transacaoService.salvarTransacao(transacaoContribuicaoDTO);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        ObjetivoResponseDTO responseDTO = toObjetivoResponseDTO(objetivoAtualizado);
        return responseDTO;
    }

    private ObjetivoResponseDTO toObjetivoResponseDTO(Objetivo objetivo) {
        ObjetivoResponseDTO dto = new ObjetivoResponseDTO();
        dto.setId(objetivo.getId());
        dto.setNome(objetivo.getNome());
        dto.setValorMeta(objetivo.getValorMeta());
        dto.setValorAtual(objetivo.getValorAtual());
        dto.setDataCriacao(objetivo.getDataCriacao());
        dto.setDataConclusaoPrevista(objetivo.getDataConclusaoPrevista());
        dto.setStatus(objetivo.getStatus());
        if (objetivo.getUsuario() != null) {
            dto.setUsuarioId(objetivo.getUsuario().getId());
        }
        return dto;
    }
}
