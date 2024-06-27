package com.mobiauto.controller;

import com.mobiauto.model.Revenda;
import com.mobiauto.service.RevendaService;
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
public class RevendaControllerTest {

    @InjectMocks
    RevendaController controller;

    @Mock
    RevendaService service;

    Revenda revenda;

    Revenda revendaComId;

    List<Revenda> revendas = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        revenda = Revenda.builder()
                .cnpj("123456789123")
                .nomeSocial("Revendedora Teste")
                .build();

        revendaComId = Revenda.builder()
                .id(1L)
                .cnpj("123456789123")
                .nomeSocial("Revendedora Teste")
                .build();

        revendas.add(revenda);
    }

    @Test
    void buscarRevendas() {
        when(service.findAll()).thenReturn(revendas);

        ResponseEntity<Object> responseRevendas = controller.buscarRevendas();

        assertEquals(ResponseEntity.ok(revendas), responseRevendas);
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

}