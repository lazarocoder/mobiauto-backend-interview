package com.mobiauto.service.implementation;


import com.mobiauto.model.Revenda;
import com.mobiauto.model.Role;
import com.mobiauto.model.Usuario;
import com.mobiauto.service.repository.RoleRepository;
import com.mobiauto.service.repository.UsuarioRepository;
import com.mobiauto.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Usuario findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Usuario findByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Usuario> findAllInRevenda(Long idRevenda) {
        return repository.findAll().stream()
                .filter(u -> Objects.equals(Optional.ofNullable(u.getLojaAssociada())
                .map(Revenda::getId).orElse(null), idRevenda))
                .collect(Collectors.toList());
    }

    @Override
    public Usuario save(Usuario usuario) {
        validarCadastro(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setHorarioUltimaOportunidade(new Date());
        usuario.setRoles(obterRolesPorCargo(usuario.getCargo().ordinal()));
        return repository.save(usuario);
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {
        Usuario usuarioExistente = validarEdicao(id, usuario);
        atualizarDadosUsuario(usuarioExistente, usuario);
        return repository.save(usuarioExistente);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }
        repository.deleteById(id);
    }

    private void validarCadastro(Usuario usuario) {
        if (usuario.getId() != null) {
            throw new IllegalArgumentException("Tentativa de passar id em cadastro.");
        }
    }

    private Usuario validarEdicao(Long id, Usuario usuario) {
        if (usuario.getId() != null) {
            throw new IllegalArgumentException("Tentativa de passar id ao editar.");
        }
        return findById(id);
    }

    private void atualizarDadosUsuario(Usuario usuarioExistente, Usuario usuarioNovo) {
        if (usuarioNovo.getSenha() != null) {
            usuarioExistente.setSenha(passwordEncoder.encode(usuarioNovo.getSenha()));
        }
        usuarioExistente.setRoles(obterRolesPorCargo(usuarioNovo.getCargo().ordinal()));
        usuarioExistente.setEmail(usuarioNovo.getEmail());
        usuarioExistente.setNome(usuarioNovo.getNome());
        usuarioExistente.setLojaAssociada(usuarioNovo.getLojaAssociada());
        usuarioExistente.setCargo(usuarioNovo.getCargo());
    }

    private List<Role> obterRolesPorCargo(int cargoOrdinal) {
        List<Role> roles = roleRepository.findAll();
        return roles.subList(cargoOrdinal, roles.size());
    }
}
