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

    @Test
    void buscarRevendaPorId() {
        when(service.findById(revenda.getId())).thenReturn(revenda);

        ResponseEntity<Object> responseRevenda = controller.buscarRevendaPorId(revenda.getId());

        assertEquals(ResponseEntity.ok(revenda), responseRevenda);

        verify(service).findById(revenda.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(revenda.getId())).thenReturn(null);
        responseRevenda = controller.buscarRevendaPorId(revenda.getId());

        assertEquals(ResponseEntity.notFound().build(), responseRevenda);
    }

    @Test
    void cadastrarRevenda() {
        when(service.save(revenda)).thenReturn(revenda);

        ResponseEntity<Object> responseRevenda = controller.cadastrarRevenda(revenda);
        assertEquals(ResponseEntity.ok(revenda), responseRevenda);
        verify(service).findByCnpj(revenda.getCnpj());
        verify(service).save(revenda);
        verifyNoMoreInteractions(service);

        responseRevenda = controller.cadastrarRevenda(revendaComId);
        assertEquals(ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado na realização do cadastro."), responseRevenda);
        verify(service, never()).save(revendaComId);

        when(service.findByCnpj(revenda.getCnpj())).thenReturn(revenda);
        responseRevenda = controller.cadastrarRevenda(revenda);
        assertEquals(ResponseEntity.badRequest().body("O CNPJ informado já possui cadastro atualmente."), responseRevenda);
        verify(service, never()).save(revendaComId);
    }

    @Test
    void deletarRevendaPorId() {

        when(service.findById(revenda.getId())).thenReturn(revenda);
        ResponseEntity<Object> responseRevenda = controller.deletarRevendaPorId(revenda.getId());

        assertEquals(ResponseEntity.ok().body("Revenda excluída com sucesso."), responseRevenda);
        verify(service).findById(revenda.getId());
        verify(service).delete(revenda.getId());
        verifyNoMoreInteractions(service);

        when(service.findById(revenda.getId())).thenReturn(null);
        responseRevenda = controller.deletarRevendaPorId(revenda.getId());
        assertEquals(ResponseEntity.notFound().build(), responseRevenda);

    }

}