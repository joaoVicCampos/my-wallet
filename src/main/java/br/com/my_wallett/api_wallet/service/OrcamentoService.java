package br.com.my_wallett.api_wallet.service;

import br.com.my_wallett.api_wallet.dto.OrcamentoRequestoDTO;
import br.com.my_wallett.api_wallet.dto.OrcamentoResponseDTO;
import br.com.my_wallett.api_wallet.model.Categoria;
import br.com.my_wallett.api_wallet.model.Orcamento;
import br.com.my_wallett.api_wallet.model.Usuario;
import br.com.my_wallett.api_wallet.repository.CategoriaRepository;
import br.com.my_wallett.api_wallet.repository.OrcamentoRepository;
import br.com.my_wallett.api_wallet.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrcamentoService {

    @Autowired
    OrcamentoRepository orcamentoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Transactional
    public OrcamentoResponseDTO criarOrcamento(OrcamentoRequestoDTO orcamentoRequestoDTO){
        Usuario usuario = usuarioRepository.findById(orcamentoRequestoDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com Id" + orcamentoRequestoDTO.getUsuarioId()));

        Categoria categoria = categoriaRepository.findById(orcamentoRequestoDTO.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada com Id" + orcamentoRequestoDTO.getCategoriaId()));

        Optional<Orcamento> orcamentoExistente = orcamentoRepository.findByUsuarioIdAndCategoriaIdAndMesAndAno(
                orcamentoRequestoDTO.getUsuarioId(),
                orcamentoRequestoDTO.getCategoriaId(),
                orcamentoRequestoDTO.getMes(),
                orcamentoRequestoDTO.getAno()
        );
        if (orcamentoExistente.isPresent()) {
            throw new IllegalArgumentException("Já existe um orçamento para este usuário, categoria, mês e ano.");
        }

        Orcamento orcamento = new Orcamento();
        orcamento.setLimiteMensal(orcamentoRequestoDTO.getLimiteMensal());
        orcamento.setMes(orcamentoRequestoDTO.getMes());
        orcamento.setAno(orcamentoRequestoDTO.getAno());
        orcamento.setUsuario(usuario);
        orcamento.setCategoria(categoria);

        Orcamento orcamentoCriado =  orcamentoRepository.save(orcamento);
        return toOrcamentoResponseDTO(orcamentoCriado);
    }

    @Transactional(readOnly = true)
    public Optional<OrcamentoResponseDTO> buscarOrcamentoPorIdEUsuario(Long orcamentoId, Long usuarioIdLogado){
        Optional<Orcamento> orcamentoEncontrado = orcamentoRepository.findByIdAndUsuarioId(orcamentoId, usuarioIdLogado);
        return orcamentoEncontrado.map(this::toOrcamentoResponseDTO);
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> buscarOrcamentos(){
        List<Orcamento> orcamentos = orcamentoRepository.findAll();
        return orcamentos
                .stream()
                .map(this::toOrcamentoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> buscarPorUsuarioId(Long usuarioId){
        if (!usuarioRepository.existsById(usuarioId)){
            List.of();
        }
        List<Orcamento> orcamentoPorUsuario = orcamentoRepository.findByUsuarioId(usuarioId);
        return orcamentoPorUsuario
                .stream()
                .map(this::toOrcamentoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrcamentoResponseDTO> buscarPorUsuarioMesEAno(Long usuarioId, int mes, int ano){
        if (!usuarioRepository.existsById(usuarioId)){
            List.of();
        }
        List<Orcamento> orcamentoPorUsuarioMesAno = orcamentoRepository.findByUsuarioIdAndMesAndAno(usuarioId, mes, ano);
        return orcamentoPorUsuarioMesAno
                .stream()
                .map(this::toOrcamentoResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<OrcamentoResponseDTO> atualizarOrcamento(Long orcamentoId, OrcamentoRequestoDTO orcamentoRequestoDTO, Long usuarioIdLogado){
        return orcamentoRepository.findByIdAndUsuarioId(orcamentoId, usuarioIdLogado)
                    .map(orcamentoExistente -> {
                        if (orcamentoRequestoDTO.getCategoriaId() != null && !orcamentoRequestoDTO.getCategoriaId().equals(orcamentoRequestoDTO.getCategoriaId())){
                            throw new IllegalArgumentException("Não é permitido alterar a categoria de um orçamento existente.");
                        }
                        if (orcamentoRequestoDTO.getMes() != 0 && orcamentoRequestoDTO.getMes() != orcamentoExistente.getMes()){
                            throw new IllegalArgumentException("Não é permitido alterar o mês de um orçamento existente.");
                        }
                        if (orcamentoRequestoDTO.getAno() != 0 && orcamentoRequestoDTO.getAno() != orcamentoExistente.getAno()){
                            throw new IllegalArgumentException("Não é permitido alterar o ano de um orçamento existente.");
                        }
                        orcamentoExistente.setLimiteMensal(orcamentoRequestoDTO.getLimiteMensal());
                        Orcamento orcamentotualizado = orcamentoRepository.save(orcamentoExistente);
                        return toOrcamentoResponseDTO(orcamentotualizado);
                });
    }

    @Transactional
    public boolean deletarOrcamento(Long orcamentoId, Long usuarioIdLogado){
        Optional<Orcamento> orcamentoOptional = orcamentoRepository.findByIdAndUsuarioId(orcamentoId, usuarioIdLogado);
        if (orcamentoOptional.isPresent()){
            orcamentoRepository.delete(orcamentoOptional.get());
            return true;
        }
        return false;
    }

    public OrcamentoResponseDTO toOrcamentoResponseDTO(Orcamento orcamento){
        OrcamentoResponseDTO dto = new OrcamentoResponseDTO();
        dto.setId(orcamento.getId());
        dto.setLimiteMensal(orcamento.getLimiteMensal());
        dto.setMes(orcamento.getMes());
        dto.setAno(orcamento.getAno());
        if (orcamento.getUsuario() != null){
            dto.setUsuarioId(orcamento.getUsuario().getId());
            dto.setUsuarioNome(orcamento.getUsuario().getNome());
        }
        if (orcamento.getCategoria() != null) {
            dto.setCategoriaId(orcamento.getCategoria().getId());
            dto.setCategoriaNome(orcamento.getCategoria().getNome());
        }
        return dto;
    }
}
