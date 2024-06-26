package com.mobiauto.controller;

import com.mobiauto.enumerated.Cargo;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Role;
import com.mobiauto.model.Usuario;
import com.mobiauto.security.UserPrincipal;
import com.mobiauto.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioControllerTest {

    @InjectMocks
    UsuarioController controller;

    @Mock
    UsuarioService service;

    UserPrincipal userPrincipal;

    Usuario usuario;

    Usuario usuarioComId;

    List<Usuario> usuarios = new ArrayList<>();

    List<Role> roles = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        usuario = Usuario.builder().nome("teste").email("teste@email.com").senha("123").cargo(Cargo.ADMINISTRADOR).lojaAssociada(Revenda.builder().id(1L).cnpj("432432556").nomeSocial("Revendedora Teste").build()).roles(roles).build();

        usuarioComId = Usuario.builder().id(1L).nome("teste").email("teste@email.com").senha("123").cargo(Cargo.ADMINISTRADOR).lojaAssociada(Revenda.builder().id(2L).cnpj("432432556").nomeSocial("Revendedora Teste").build()).roles(roles).build();

        userPrincipal = UserPrincipal.create(usuario);

        roles.add(Role.builder().id(1L).name("NIVEL_ADMINISTRADOR").build());

        usuarios.add(usuario);
    }

    @Test
    void buscarUsuarios() {
        when(service.findAll()).thenReturn(usuarios);

        ResponseEntity<Object> responseUsuarios = controller.buscarUsuarios();

        assertEquals(ResponseEntity.ok(usuarios), responseUsuarios);
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void buscarUsuariosDaRevenda() {
        when(service.findByEmail(userPrincipal.getUsername())).thenReturn(usuario);
        when(service.findAllInRevenda(usuario.getLojaAssociada().getId())).thenReturn(usuarios);

        ResponseEntity<Object> responseUsuarios = controller.buscarUsuariosDaRevenda(userPrincipal);

        assertEquals(ResponseEntity.ok(usuarios), responseUsuarios);
        verify(service).findAllInRevenda(usuario.getLojaAssociada().getId());
        verifyNoMoreInteractions(service);

        usuario.setLojaAssociada(null);
        responseUsuarios = controller.buscarUsuariosDaRevenda(userPrincipal);

        assertEquals(ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada para que possa realizar a busca."), responseUsuarios);
        verify(service, never()).findAllInRevenda(null);
    }

    @Test
    void buscarUsuarioPorId() {
        when(service.findById(usuario.getId())).thenReturn(usuario);

        ResponseEntity<Object> responseUsuario = controller.buscarUsuarioPorId(usuario.getId());

        assertEquals(ResponseEntity.ok(usuario), responseUsuario);

        verify(service).findById(usuario.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(usuario.getId())).thenReturn(null);
        responseUsuario = controller.buscarUsuarioPorId(usuario.getId());

        assertEquals(ResponseEntity.notFound().build(), responseUsuario);
    }

    @Test
    void cadastrarUsuario() {
        when(service.save(usuario)).thenReturn(usuario);

        ResponseEntity<Object> responseUsuario = controller.cadastrarUsuario(usuario);
        assertEquals(ResponseEntity.ok(usuario), responseUsuario);
        verify(service).findByEmail(usuario.getEmail());
        verify(service).save(usuario);
        verifyNoMoreInteractions(service);

        responseUsuario = controller.cadastrarUsuario(usuarioComId);
        assertEquals(ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado em cadastro."), responseUsuario);
        verify(service, never()).save(usuarioComId);

        when(service.findByEmail(usuario.getEmail())).thenReturn(usuario);
        responseUsuario = controller.cadastrarUsuario(usuario);
        assertEquals(ResponseEntity.badRequest().body("O email informado já possui cadastro."), responseUsuario);
        verify(service, never()).save(usuarioComId);
    }

    @Test
    void cadastrarUsuarioEmRevenda() {
        when(service.findByEmail(usuario.getEmail())).thenReturn(null).thenReturn(usuario);
        when(service.save(usuario)).thenReturn(usuario);

        ResponseEntity<Object> responseUsuario = controller.cadastrarUsuarioEmRevenda(usuario, userPrincipal);

        assertEquals(ResponseEntity.ok(usuario), responseUsuario);
        verify(service, times(2)).findByEmail(usuario.getEmail());
        verify(service).save(usuario);
        verifyNoMoreInteractions(service);

        usuario.setLojaAssociada(null);
        when(service.findByEmail(usuario.getEmail())).thenReturn(null).thenReturn(usuario);
        responseUsuario = controller.cadastrarUsuarioEmRevenda(usuario, userPrincipal);
        assertEquals(ResponseEntity.badRequest().body("O usuário precisa ter uma loja que seja associada ao mesmo para realizar o cadastro."), responseUsuario);
    }

    @Test
    void editarUsuarioEmRevenda() {
        when(service.findByEmail(usuario.getEmail())).thenReturn(null).thenReturn(usuario);
        when(service.findById(usuario.getId())).thenReturn(usuario);
        when(service.update(usuario.getId(), usuario)).thenReturn(usuario);

        ResponseEntity<Object> responseUsuario = controller.editarUsuarioEmRevenda(usuario.getId(), usuario, userPrincipal);

        assertEquals(ResponseEntity.ok(usuario), responseUsuario);
        verify(service, times(2)).findByEmail(usuario.getEmail());
        verify(service, times(2)).findById(usuario.getId());
        verify(service).update(usuario.getId(), usuario);
        verifyNoMoreInteractions(service);

        when(service.findByEmail(usuario.getEmail())).thenReturn(null).thenReturn(usuario);
        when(service.findById(usuario.getId())).thenReturn(usuario).thenReturn(usuarioComId);

        responseUsuario = controller.editarUsuarioEmRevenda(usuario.getId(), usuario, userPrincipal);
        assertEquals(ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada correspondente à loja do usuário a ser editado."), responseUsuario);
    }

    @Test
    void deletarUsuarioPorId() {
        when(service.findById(usuario.getId())).thenReturn(usuario);

        ResponseEntity<Object> responseUsuario = controller.deletarUsuarioPorId(usuario.getId());

        assertEquals(ResponseEntity.ok().body("Usuário excluído com sucesso."), responseUsuario);
        verify(service).findById(usuario.getId());
        verify(service).delete(usuario.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(usuario.getId())).thenReturn(null);
        responseUsuario = controller.deletarUsuarioPorId(usuario.getId());
        assertEquals(ResponseEntity.notFound().build(), responseUsuario);

    }

}