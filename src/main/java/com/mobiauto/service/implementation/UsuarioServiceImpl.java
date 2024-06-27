package com.mobiauto.service.implementation;


import com.mobiauto.exception.EntidadeNaoEncontradaException;
import com.mobiauto.exception.ValidacaoException;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Role;
import com.mobiauto.model.Usuario;
import com.mobiauto.security.UserPrincipal;
import com.mobiauto.service.UsuarioService;
import com.mobiauto.service.repository.RoleRepository;
import com.mobiauto.service.repository.UsuarioRepository;
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

        var usuario = repository.findById(id).orElse(null);

        if(usuario == null){
            throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        }

        return usuario;
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
    public List<Usuario> buscarUsuariosDaRevenda(UserPrincipal userPrincipal) {
        Revenda revenda = getUsuarioAutenticado(userPrincipal).getLojaAssociada();
        if (revenda == null) {
            throw new ValidacaoException("O usuário precisa ter uma loja associada para que possa realizar a busca.");
        }

        return repository.findAll().stream()
                .filter(u -> Objects.equals(Optional.ofNullable(u.getLojaAssociada())
                        .map(Revenda::getId).orElse(null), revenda.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Usuario save(Usuario usuario) {

       validarCadastro(usuario);

        return saveUsuario(usuario);
    }

    private void validateUsuario(Usuario usuario){
        if (usuario.getId() != null) {
            throw new ValidacaoException("O parâmetro 'id' não pode ser informado em cadastro.");
        }

        var usuarioPorEmail = findByEmail(usuario.getEmail());

        if (usuarioPorEmail != null) {
            throw new ValidacaoException("O email informado já possui cadastro.");
        }
    }

    private Usuario saveUsuario(Usuario usuario){
        validarCadastro(usuario);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuario.setHorarioUltimaOportunidade(new Date());
        usuario.setRoles(obterRolesPorCargo(usuario.getCargo().ordinal()));
        return repository.save(usuario);
    }

    @Override
    public Usuario cadastrarUsuarioEmRevenda(Usuario usuario, UserPrincipal userPrincipal) {
        validarCadastro(usuario);

        Usuario usuarioAutenticado = getUsuarioAutenticado(userPrincipal);
        if (usuarioAutenticado.getLojaAssociada() == null) {
            throw new ValidacaoException("O usuário precisa ter uma loja que seja associada ao mesmo para realizar o cadastro.");
        }

        usuario.setLojaAssociada(usuarioAutenticado.getLojaAssociada());

        return saveUsuario(usuario);
    }

    private Usuario getUsuarioAutenticado(UserPrincipal userPrincipal) {
        return findByEmail(userPrincipal.getUsername());
    }

    @Override
    public Usuario update(Long id, Usuario usuario) {
        var usuarioExistente = validateUpdate(id, usuario);

        return updateUsuario(usuarioExistente, usuario);
    }

    @Override
    public Usuario editarUsuarioEmRevenda(Long id, Usuario usuario, UserPrincipal userPrincipal) {

        var usuarioExistente = validateUpdate(id, usuario);

        Usuario usuarioAutenticado = getUsuarioAutenticado(userPrincipal);
        Long idRevendaUsuarioAutenticado = usuarioAutenticado.getLojaAssociada().getId();
        Long idRevendaUsuarioNovo = findById(id).getLojaAssociada().getId();

        if (!Objects.equals(idRevendaUsuarioAutenticado, idRevendaUsuarioNovo)) {
            throw new ValidacaoException("O usuário precisa ter uma loja associada correspondente à loja do usuário a ser editado.");
        }

        return updateUsuario(usuarioExistente, usuario);
    }

    private Usuario validateUpdate(Long id, Usuario usuario){
        var usuarioPorEmail = findByEmail(usuario.getEmail());

        if (usuarioPorEmail != null && !Objects.equals(id, usuarioPorEmail.getId())) {
            throw new ValidacaoException("O email informado já possui cadastro.");
        }

        var usuarioExistente = findById(id);

        if (usuarioExistente == null) {
            throw new EntidadeNaoEncontradaException("Usuário não encontrado.");
        }

        return usuarioExistente;
    }

    private Usuario updateUsuario(Usuario usuarioExistente, Usuario usuario) {
        atualizarDadosUsuario(usuarioExistente, usuario);
        return repository.save(usuarioExistente);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Usuário não encontrado.");
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
