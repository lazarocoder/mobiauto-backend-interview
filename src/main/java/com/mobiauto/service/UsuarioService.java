package com.mobiauto.service;

import com.mobiauto.model.Usuario;
import java.util.List;

public interface UsuarioService {

    Usuario findById(Long id);

    Usuario findByEmail(String email);

    List<Usuario> findAll();

    List<Usuario> findAllInRevenda(Long idRevenda);

    Usuario save(Usuario obj);

    Usuario update(Long id, Usuario obj);

    void delete(Long id);
}
