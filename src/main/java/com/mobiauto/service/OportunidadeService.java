package com.mobiauto.service;


import com.mobiauto.dto.CadastroOportunidadeDto;
import com.mobiauto.model.Oportunidade;
import com.mobiauto.security.UserPrincipal;
import java.util.List;

public interface OportunidadeService {

    Oportunidade findById(Long id);

    List<Oportunidade> findAll();

    List<Oportunidade> findAllInRevenda(Long idRevenda);

    List<Oportunidade>  buscarOportunidadesDaRevenda( UserPrincipal userPrincipal);

    Oportunidade save(CadastroOportunidadeDto cadastroOportunidadeDto);

    Oportunidade atender(CadastroOportunidadeDto cadastroOportunidadeDto, UserPrincipal userPrincipal);

    Oportunidade update(Long id, Oportunidade obj);

    Oportunidade editarOportunidadeEmRevenda(Long id, Oportunidade oportunidade, UserPrincipal userPrincipal);

    Oportunidade editarOportunidadeAssociada(Long id, Oportunidade oportunidade, UserPrincipal userPrincipal);

    void delete(Long id);
}