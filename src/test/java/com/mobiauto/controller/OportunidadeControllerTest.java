package com.mobiauto.controller;

import com.mobiauto.dto.CadastroOportunidadeDto;
import com.mobiauto.enumerated.Cargo;
import com.mobiauto.enumerated.Status;
import com.mobiauto.model.Oportunidade;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Role;
import com.mobiauto.model.Usuario;
import com.mobiauto.security.UserPrincipal;
import com.mobiauto.service.OportunidadeService;
import com.mobiauto.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OportunidadeControllerTest {

    private static final String TEST_EMAIL = "teste@email.com";
    private static final String TEST_EMAIL_2 = "teste2@email.com";
    private static final String CLIENT_EMAIL = "cliente@email.com";
    private static final String TEST_NAME = "teste";
    private static final String TEST_NAME_2 = "teste2";
    private static final String CLIENT_NAME = "Cliente";
    private static final String PHONE_NUMBER = "946474844";
    private static final String CAR_BRAND = "Mobi";
    private static final String CAR_MODEL = "Mobicar";
    private static final String CAR_VERSION = "1.0";
    private static final String ROLE_NAME = "NIVEL_ADMINISTRADOR";
    private static final String COMPANY_NAME = "Revendedora Teste";
    private static final String COMPANY_CNPJ = "123456789";
    private static final String COMPANY_CNPJ_2 = "321456789";

    @InjectMocks
    private OportunidadeController controller;

    @Mock
    private OportunidadeService service;

    @Mock
    private UsuarioService usuarioService;

    private Oportunidade oportunidade, oportunidade2, oportunidadeComId;
    private Usuario usuario, usuario2;
    private Revenda revenda, revenda2;
    private UserPrincipal userPrincipal;
    private List<Oportunidade> oportunidades;
    private List<Usuario> usuarios;
    private List<Role> roles;

    @BeforeEach
    public void setUp() {
        roles = new ArrayList<>();
        roles.add(Role.builder().id(1L).name(ROLE_NAME).build());

        revenda = Revenda.builder().id(1L).cnpj(COMPANY_CNPJ).nomeSocial(COMPANY_NAME).build();
        revenda2 = Revenda.builder().id(2L).cnpj(COMPANY_CNPJ_2).nomeSocial(COMPANY_NAME).build();

        usuario = Usuario.builder().id(1L).nome(TEST_NAME).email(TEST_EMAIL).senha("123")
                .cargo(Cargo.ASSISTENTE).lojaAssociada(revenda).roles(roles)
                .horarioUltimaOportunidade(new Date()).build();

        usuario2 = Usuario.builder().id(2L).nome(TEST_NAME_2).email(TEST_EMAIL_2).senha("123")
                .cargo(Cargo.ASSISTENTE).lojaAssociada(revenda2).roles(roles)
                .horarioUltimaOportunidade(new Date()).build();

        oportunidade = Oportunidade.builder().status(Status.NOVO).nomeCliente(CLIENT_NAME)
                .emailCliente(CLIENT_EMAIL).telefoneCliente(PHONE_NUMBER).marcaVeiculo(CAR_BRAND)
                .modeloVeiculo(CAR_MODEL).versaoVeiculo(CAR_VERSION).anoVeiculo(2018)
                .lojaAssociada(revenda).usuarioAssociado(usuario).build();

        oportunidade2 = Oportunidade.builder().status(Status.NOVO).nomeCliente(CLIENT_NAME)
                .emailCliente(CLIENT_EMAIL).telefoneCliente(PHONE_NUMBER).marcaVeiculo(CAR_BRAND)
                .modeloVeiculo(CAR_MODEL).versaoVeiculo(CAR_VERSION).anoVeiculo(2018)
                .lojaAssociada(revenda).usuarioAssociado(usuario2).build();

        oportunidadeComId = Oportunidade.builder().id(1L).status(Status.NOVO).nomeCliente(CLIENT_NAME)
                .emailCliente(CLIENT_EMAIL).telefoneCliente(PHONE_NUMBER).marcaVeiculo(CAR_BRAND)
                .modeloVeiculo(CAR_MODEL).versaoVeiculo(CAR_VERSION).anoVeiculo(2018)
                .lojaAssociada(revenda).usuarioAssociado(usuario).build();

        userPrincipal = UserPrincipal.create(usuario);
        oportunidades = new ArrayList<>();
        oportunidades.add(oportunidadeComId);
        usuarios = new ArrayList<>();
        usuarios.add(usuario);
    }

    @Test
    void buscarOportunidades() {
        when(service.findAll()).thenReturn(oportunidades);

        ResponseEntity<Object> responseOportunidades = controller.buscarOportunidades();

        assertEquals(ResponseEntity.ok(oportunidades), responseOportunidades);
        verify(service).findAll();
        verifyNoMoreInteractions(service);
    }

    @Test
    void buscarOportunidadesDaRevenda() {

        when(service.buscarOportunidadesDaRevenda(userPrincipal)).thenReturn(oportunidades);

        ResponseEntity<Object> responseOportunidades = controller.buscarOportunidadesDaRevenda(userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidades), responseOportunidades);

    }

    @Test
    void buscarOportunidadePorId() {
        when(service.findById(oportunidade.getId())).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.buscarOportunidadePorId(oportunidade.getId());

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);

    }

    @Test
    void cadastrarOportunidade() {
        var cadastroOportunidadeDto = CadastroOportunidadeDto.builder()
                .status(Status.NOVO)
                .nomeCliente("José da Silva")
                .emailCliente("josesilva@gmail.com")
                .build();

        when(service.save(cadastroOportunidadeDto)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.cadastrarOportunidade(cadastroOportunidadeDto);

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
    }

    @Test
    void atenderOportunidade() {
        var cadastroOportunidadeDto = CadastroOportunidadeDto.builder()
                .nomeCliente("José da Silva")
                .emailCliente("josesilva@gmail.com")
                .build();

        when(service.atender(cadastroOportunidadeDto, userPrincipal)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.atenderOportunidade(cadastroOportunidadeDto, userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
    }

    @Test
    void editarOportunidade() {
        var oportunidadeAtual = Oportunidade.builder()
                .id(1L)
                .nomeCliente("José da Silva")
                .emailCliente("josesilva@gmail.com")
                .build();

        when(service.update(oportunidadeAtual.getId(), oportunidadeAtual)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.editarOportunidade(oportunidadeAtual.getId(), oportunidadeAtual);

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
    }

    @Test
    void editarOportunidadeEmRevenda() {
        var oportunidadeAtual = Oportunidade.builder()
                .id(1L)
                .nomeCliente("José da Silva")
                .emailCliente("josesilva@gmail.com")
                .build();

        when(service.editarOportunidadeEmRevenda(oportunidadeAtual.getId(), oportunidadeAtual, userPrincipal)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.editarOportunidadeEmRevenda(oportunidadeAtual.getId(), oportunidadeAtual, userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
    }

    @Test
    void editarOportunidadeAssociada() {
        var oportunidadeAtual = Oportunidade.builder()
                .id(1L)
                .nomeCliente("José da Silva")
                .emailCliente("josesilva@gmail.com")
                .build();

        when(service.editarOportunidadeAssociada(oportunidadeAtual.getId(), oportunidadeAtual, userPrincipal)).thenReturn(oportunidade);

        ResponseEntity<Object> responseOportunidade = controller.editarOportunidadeAssociada(oportunidadeAtual.getId(), oportunidadeAtual, userPrincipal);

        assertEquals(ResponseEntity.ok(oportunidade), responseOportunidade);
    }

}
