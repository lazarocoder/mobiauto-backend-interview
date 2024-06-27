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


}