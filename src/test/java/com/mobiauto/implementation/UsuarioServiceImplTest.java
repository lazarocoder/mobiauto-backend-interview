package com.mobiauto.implementation;

import com.mobiauto.enumerated.Cargo;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Usuario;
import com.mobiauto.service.repository.RoleRepository;
import com.mobiauto.service.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {

    @InjectMocks
    com.mobiauto.service.implementation.UsuarioServiceImpl service;

    @Mock
    UsuarioRepository repository;

    @Mock
    RoleRepository roleRepository;
    Usuario usuario;

    Usuario usuarioComId;

    List<Usuario> usuarios = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        usuario = Usuario.builder()
                .nome("teste")
                .email("teste@email.com")
                .senha("123")
                .cargo(Cargo.ADMINISTRADOR)
                .lojaAssociada(Revenda.builder()
                .id(1L)
                .cnpj("12345678912345")
                .nomeSocial("Revendedora Teste")
                .build())
                .build();

        usuarioComId = Usuario.builder()
                .id(1L)
                .nome("teste")
                .email("teste@email.com")
                .senha("123")
                .cargo(Cargo.ADMINISTRADOR)
                .lojaAssociada(Revenda.builder()
                .id(1L)
                .cnpj("32145678912345")
                .nomeSocial("Revendedora Teste")
                .build())
                .build();

        usuarios.add(usuarioComId);
    }

    @Test
    void findById() {
        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));

        Usuario usuarioRetornado = service.findById(usuario.getId());

        assertEquals(Optional.of(usuario).get(), usuarioRetornado);
        verify(repository).findById(usuario.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByEmail() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Usuario usuarioRetornado = service.findByEmail(usuario.getEmail());

        assertEquals(Optional.of(usuario).get(), usuarioRetornado);
    }

    @Test
    void findByIdEmail() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);

        Usuario usuarioRetornado = service.findByEmail(usuario.getEmail());

        assertEquals(Optional.of(usuario).get(), usuarioRetornado);
        verify(repository).findByEmail(usuario.getEmail());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(usuarios);

        List<Usuario> listUsuarios = service.findAll();

        assertEquals(usuarios, listUsuarios);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAllInRevenda() {
        when(repository.findAll().stream().filter(u -> Objects.equals((u.getLojaAssociada() != null) ? u.getLojaAssociada().getId() : null, usuario.getLojaAssociada().getId())).toList()).thenReturn(usuarios);

        List<Usuario> listUsuarios = service.findAllInRevenda(usuario.getLojaAssociada().getId());

        assertEquals(usuarios, listUsuarios);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save() {
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario usuarioRetornado = service.save(usuario);
        assertEquals(usuario, usuarioRetornado);

        verify(repository).save(usuario);
        verify(roleRepository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update() {
        when(repository.findByEmail(usuario.getEmail())).thenReturn(usuario);
        when(repository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        when(repository.save(usuario)).thenReturn(usuario);

        Usuario usuarioRetornado = service.update(usuario.getId(), usuario);
        assertEquals(Optional.of(usuario).get(), usuarioRetornado);

    }

}