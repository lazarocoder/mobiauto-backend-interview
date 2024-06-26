package com.mobiauto.service.repository;

import com.mobiauto.model.Revenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevendaRepository extends JpaRepository<Revenda, Long> {
    Revenda findByCnpj(String cnpj);
}