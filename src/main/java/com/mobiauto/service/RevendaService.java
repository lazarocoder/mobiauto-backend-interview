package com.mobiauto.service;

import com.mobiauto.model.Revenda;
import java.util.List;

public interface RevendaService {

    Revenda findById(Long id);

    Revenda findByCnpj(String cnpj);

    List<Revenda> findAll();

    Revenda save(Revenda obj);

    Revenda update(Long id, Revenda obj);

    void delete(Long id);
}
