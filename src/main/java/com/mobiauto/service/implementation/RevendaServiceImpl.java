package com.mobiauto.service.implementation;

import com.mobiauto.exception.EntidadeNaoEncontradaException;
import com.mobiauto.exception.ValidacaoException;
import com.mobiauto.model.Revenda;
import com.mobiauto.service.RevendaService;
import com.mobiauto.service.repository.RevendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RevendaServiceImpl implements RevendaService {

    private final RevendaRepository repository;

    @Override
    public Revenda findById(Long id) {
        var revendaBuscada = repository.findById(id).orElse(null);
        if(revendaBuscada == null){
            throw new EntidadeNaoEncontradaException("Revenda não encontrada.");
        }
        return revendaBuscada;
    }

    @Override
    public Revenda findByCnpj(String cnpj) {
        return repository.findByCnpj(cnpj);
    }

    @Override
    public List<Revenda> findAll() {
        return repository.findAll();
    }

    @Override
    public Revenda save(Revenda revenda) {
        Revenda revendaPorCnpj = findByCnpj(revenda.getCnpj());

        if (revenda.getId() != null) {
            throw new ValidacaoException("O parâmetro 'id' não pode ser informado na realização do cadastro.");
        }

        if (revendaPorCnpj != null) {
            throw new ValidacaoException("O CNPJ informado já possui cadastro atualmente.");
        }

        return repository.save(revenda);
    }

    @Override
    public Revenda update(Long id, Revenda revenda) {
        var revendaExistente = validarRevenda(revenda, id);
        atualizarDadosRevenda(revendaExistente, revenda);
        return repository.save(revendaExistente);
    }

    private Revenda validarRevenda(Revenda revenda, Long id) {
        var revendaPorCnpj = findByCnpj(revenda.getCnpj());

        if (revendaPorCnpj != null && !Objects.equals(id, revendaPorCnpj.getId())) {
            throw new ValidacaoException("O CNPJ informado já possui cadastro atualmente.");
        }

        var  revendaExistente = findById(id);

        if (revendaExistente == null) {
            throw new EntidadeNaoEncontradaException("Revenda não encontrada.");
        }

        return revendaExistente;
    }

    private void atualizarDadosRevenda(Revenda revendaExistente, Revenda revenda) {
        revendaExistente.setCnpj(revenda.getCnpj());
        revendaExistente.setNomeSocial(revenda.getNomeSocial());
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Revenda não encontrada.");
        }
        repository.deleteById(id);
    }
}
