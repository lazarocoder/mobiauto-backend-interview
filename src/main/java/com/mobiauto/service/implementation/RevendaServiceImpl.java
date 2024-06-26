package com.mobiauto.service.implementation;

import com.mobiauto.model.Revenda;
import com.mobiauto.service.repository.RevendaRepository;
import com.mobiauto.service.RevendaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevendaServiceImpl implements RevendaService {

    private final RevendaRepository repository;

    @Override
    public Revenda findById(Long id) {
        return repository.findById(id).orElse(null);
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
        if (revenda.getId() != null) {
            throw new IllegalArgumentException("Tentativa de passar id em cadastro.");
        }
        return repository.save(revenda);
    }

    @Override
    public Revenda update(Long id, Revenda revenda) {
        Revenda revendaExistente = findById(id);
        if (revendaExistente == null) {
            throw new IllegalArgumentException("Revenda não encontrada no momento.");
        }
        atualizarDadosRevenda(revendaExistente, revenda);
        return repository.save(revendaExistente);
    }

    private void atualizarDadosRevenda(Revenda revendaExistente, Revenda revenda) {
        revendaExistente.setCnpj(revenda.getCnpj());
        revendaExistente.setNomeSocial(revenda.getNomeSocial());
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Revenda não encontrada no momento.");
        }
        repository.deleteById(id);
    }
}
