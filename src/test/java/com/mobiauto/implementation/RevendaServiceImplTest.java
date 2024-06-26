package com.mobiauto.implementation;

import com.mobiauto.model.Revenda;
import com.mobiauto.service.repository.RevendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class RevendaServiceImplTest {

    @InjectMocks
    com.mobiauto.service.implementation.RevendaServiceImpl service;

    @Mock
    RevendaRepository repository;

    Revenda revenda;

    Revenda revendaComId;
    List<Revenda> revendas = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        revenda = Revenda.builder()
                .cnpj("12345678912345")
                .nomeSocial("Revendedora Teste")
                .build();

        revendaComId = Revenda.builder()
                .id(1L)
                .cnpj("32145678912345")
                .nomeSocial("Revendedora Teste")
                .build();

        revendas.add(revenda);
    }

    @Test
    void findById() {
        when(repository.findById(revenda.getId())).thenReturn(Optional.of(revenda));

        Revenda revendaRetornado = service.findById(revenda.getId());

        assertEquals(Optional.of(revenda).get(), revendaRetornado);
        verify(repository).findById(revenda.getId());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByIdNull() {
        assertNull(service.findById(0L));

        verify(repository).findById(0L);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findByIdCnpj() {
        when(repository.findByCnpj(revenda.getCnpj())).thenReturn(revenda);

        Revenda revendaRetornado = service.findByCnpj(revenda.getCnpj());

        assertEquals(Optional.of(revenda).get(), revendaRetornado);
        verify(repository).findByCnpj(revenda.getCnpj());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void findAll() {
        when(repository.findAll()).thenReturn(revendas);

        List<Revenda> listRevendas = service.findAll();

        assertEquals(revendas, listRevendas);
        verify(repository).findAll();
        verifyNoMoreInteractions(repository);
    }

    @Test
    void save() {
        when(repository.save(revenda)).thenReturn(revenda);

        Revenda revendaRetornado = service.save(revenda);
        assertEquals(revenda, revendaRetornado);

        verify(repository).save(revenda);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void update() {
        when(repository.findById(revenda.getId())).thenReturn(Optional.of(revenda));
        when(repository.save(revenda)).thenReturn(revenda);

        Revenda revendaRetornado = service.update(revenda.getId(), revenda);
        assertEquals(Optional.of(revenda).get(), revendaRetornado);

        verify(repository).save(revenda);
        verify(repository).findById(revenda.getId());
        verifyNoMoreInteractions(repository);
    }

}