package com.mobiauto.service;

import com.mobiauto.model.Usuario;
import com.mobiauto.security.UserPrincipal;

import java.util.List;

public interface UsuarioService {

    Usuario findById(Long id);

    Usuario findByEmail(String email);

    List<Usuario> findAll();

    List<Usuario> findAllInRevenda(Long idRevenda);

    List<Usuario> buscarUsuariosDaRevenda(UserPrincipal userPrincipal);

    Usuario save(Usuario obj);

    Usuario cadastrarUsuarioEmRevenda(Usuario usuario, UserPrincipal userPrincipal);

    Usuario update(Long id, Usuario obj);

    Usuario editarUsuarioEmRevenda(Long id, Usuario usuario, UserPrincipal userPrincipal);

    void delete(Long id);
}
