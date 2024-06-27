package com.mobiauto.service.implementation;


import com.mobiauto.dto.CadastroOportunidadeDto;
import com.mobiauto.enumerated.Cargo;
import com.mobiauto.enumerated.Status;
import com.mobiauto.exception.EntidadeNaoEncontradaException;
import com.mobiauto.exception.ValidacaoException;
import com.mobiauto.model.Oportunidade;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Usuario;
import com.mobiauto.security.UserPrincipal;
import com.mobiauto.service.OportunidadeService;
import com.mobiauto.service.RevendaService;
import com.mobiauto.service.UsuarioService;
import com.mobiauto.service.repository.OportunidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OportunidadeServiceImpl implements OportunidadeService {

    private final OportunidadeRepository repository;

    private final UsuarioService usuarioService;

    private final RevendaService revendaService;

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    @Override
    public Oportunidade findById(Long id) {
        var oportunidade = repository.findById(id).orElse(null);

        if(oportunidade == null){
            throw new EntidadeNaoEncontradaException("Oportunidade não encontrada.");
        }
        return oportunidade;
    }

    @Override
    public List<Oportunidade> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Oportunidade> findAllInRevenda(Long idRevenda) {
        return repository.findAll().stream()
                .filter(o -> Objects.equals(
                        Optional.ofNullable(o.getLojaAssociada()).map(Revenda::getId).orElse(null),
                        idRevenda
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<Oportunidade>  buscarOportunidadesDaRevenda( UserPrincipal userPrincipal){
        var revenda = getUsuarioAutenticado(userPrincipal).getLojaAssociada();

        if(revenda == null) {
           throw new ValidacaoException("O usuário precisa ter uma loja que seja associada ao mesmo para realizar a busca.");
        }

        return repository.findAll().stream()
                .filter(o -> Objects.equals(
                        Optional.ofNullable(o.getLojaAssociada()).map(Revenda::getId).orElse(null),
                        revenda.getId()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public Oportunidade save(CadastroOportunidadeDto cadastroOportunidadeDto) {
        var oportunidade = new Oportunidade();
        BeanUtils.copyProperties(cadastroOportunidadeDto, oportunidade);

        var lojaAssociada = revendaService.findById(cadastroOportunidadeDto.getLojaAssociadaId());

        oportunidade.setLojaAssociada(lojaAssociada);

        var usuarioAssociado = usuarioService.findById(cadastroOportunidadeDto.getUsuarioAssociadoId());
        oportunidade.setUsuarioAssociado(usuarioAssociado);

        if (usuarioAssociado != null) {
           usuarioAssociado.setHorarioUltimaOportunidade(new Date());
        }

        if (oportunidade.getUsuarioAssociado() != null && oportunidade.getDataAtribuicao() == null) {
            oportunidade.setDataAtribuicao(LocalDate.now(ZONE_ID));
        }

        oportunidade.setStatus(Status.NOVO);
        return repository.save(oportunidade);
    }

    @Override
    public Oportunidade atender(CadastroOportunidadeDto cadastroOportunidadeDto, UserPrincipal userPrincipal) {
        var oportunidade = new Oportunidade();
        BeanUtils.copyProperties(cadastroOportunidadeDto, oportunidade);

        var revenda = getUsuarioAutenticado(userPrincipal).getLojaAssociada();

        if (revenda == null) {
            throw new ValidacaoException("O usuário deve ter uma loja que ele seja associada ao mesmo para atender.");
        }

        oportunidade.setLojaAssociada(revenda);
        Usuario usuarioOcioso = usuarioService.findAllInRevenda(revenda.getId()).stream().filter(u -> u.getCargo() == Cargo.ASSISTENTE).min(Comparator.comparing(Usuario::getHorarioUltimaOportunidade)).orElse(null);

        if (usuarioOcioso != null) {
            usuarioOcioso.setHorarioUltimaOportunidade(new Date());
            oportunidade.setUsuarioAssociado(usuarioOcioso);
        }

        if (oportunidade.getUsuarioAssociado() != null && oportunidade.getDataAtribuicao() == null) {
            oportunidade.setDataAtribuicao(LocalDate.now(ZONE_ID));
        }

        oportunidade.setStatus(Status.NOVO);
        return repository.save(oportunidade);
    }

    private Usuario getUsuarioAutenticado(UserPrincipal userPrincipal) {
        return usuarioService.findByEmail(userPrincipal.getUsername());
    }

    @Override
    public Oportunidade update(Long id, Oportunidade oportunidade) {


        var objBanco = findById(id);
        validarAtualizacao(objBanco, oportunidade);

        atualizarDadosOportunidade(objBanco, oportunidade);
        return repository.save(objBanco);
    }

    @Override
    public Oportunidade editarOportunidadeEmRevenda(Long id, Oportunidade oportunidade, UserPrincipal userPrincipal) {
        var objBanco = findById(id);

        validarAtualizacao(objBanco, oportunidade);

        Usuario usuario = getUsuarioAutenticado(userPrincipal);
        Long idRevendaUsuario = usuario.getLojaAssociada() != null ? usuario.getLojaAssociada().getId() : null;
        Long idRevendaOportunidade = findById(id).getLojaAssociada().getId();

        if (!Objects.equals(idRevendaOportunidade, idRevendaUsuario)) {
            throw new ValidacaoException("O usuário deve ter uma loja que seja associada ao mesmo para editar a oportunidade.");
        }

        atualizarDadosOportunidade(objBanco, oportunidade);
        return repository.save(objBanco);
    }

    @Override
    public Oportunidade editarOportunidadeAssociada(Long id, Oportunidade oportunidade, UserPrincipal userPrincipal) {
        var objBanco = findById(id);

        validarAtualizacao(objBanco, oportunidade);

        Long idUsuarioAutenticado = getUsuarioAutenticado(userPrincipal).getId();
        Usuario usuarioOportunidade = findById(id).getUsuarioAssociado();
        Long idUsuarioOportunidade = usuarioOportunidade != null ? usuarioOportunidade.getId() : null;
        Long idUsuarioOportunidadeNovo = oportunidade.getUsuarioAssociado() != null ? oportunidade.getUsuarioAssociado().getId() : null;

        if (!Objects.equals(idUsuarioAutenticado, idUsuarioOportunidade)) {
            throw new ValidacaoException("O assistente pode editar somente as oportunidades associadas a ele mesmo.");
        }

        if (!Objects.equals(idUsuarioOportunidade, idUsuarioOportunidadeNovo)) {
            throw new ValidacaoException("O assistente não pode transferir a oportunidade para outro usuário.");
        }

        atualizarDadosOportunidade(objBanco, oportunidade);
        return repository.save(objBanco);
    }

    private void validarAtualizacao(Oportunidade objBanco, Oportunidade oportunidade){
        if (objBanco == null) {
            throw new EntidadeNaoEncontradaException("Oportunidade não encontrada.");//return ResponseEntity.notFound().build();
        }

        if (oportunidade.getStatus() == Status.CONCLUIDO && oportunidade.getMotivoConclusao() == null) {
            throw new ValidacaoException("O motivo de conclusão deve ser informado ao concluir a oportunidade em questão.");
        }
    }

    private void atualizarDadosOportunidade(Oportunidade objBanco, Oportunidade oportunidade) {
        objBanco.setNomeCliente(oportunidade.getNomeCliente());
        objBanco.setEmailCliente(oportunidade.getEmailCliente());
        objBanco.setTelefoneCliente(oportunidade.getTelefoneCliente());
        objBanco.setMarcaVeiculo(oportunidade.getMarcaVeiculo());
        objBanco.setModeloVeiculo(oportunidade.getModeloVeiculo());
        objBanco.setVersaoVeiculo(oportunidade.getVersaoVeiculo());
        objBanco.setAnoVeiculo(oportunidade.getAnoVeiculo());
        objBanco.setStatus(oportunidade.getStatus());
        objBanco.setLojaAssociada(oportunidade.getLojaAssociada());
        objBanco.setUsuarioAssociado(oportunidade.getUsuarioAssociado());

        if (oportunidade.getUsuarioAssociado() != null && oportunidade.getDataAtribuicao() == null) {
            objBanco.setDataAtribuicao(LocalDate.now(ZONE_ID));
        } else {
            objBanco.setDataAtribuicao(oportunidade.getDataAtribuicao());
        }

        if (oportunidade.getStatus() == Status.CONCLUIDO && oportunidade.getDataConclusao() == null && oportunidade.getUsuarioAssociado() != null) {
            objBanco.setDataConclusao(LocalDate.now(ZONE_ID));
            objBanco.setMotivoConclusao("Concluído por " + objBanco.getUsuarioAssociado().getNome() + ". Motivo: " + oportunidade.getMotivoConclusao());
        } else {
            objBanco.setMotivoConclusao(oportunidade.getMotivoConclusao());
        }
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Oportunidade não encontrada no momento.");
        }
        repository.deleteById(id);
    }
}
