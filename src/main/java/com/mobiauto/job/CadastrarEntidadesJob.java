package com.mobiauto.job;

import com.mobiauto.config.NivelAcessoConfig;
import com.mobiauto.enumerated.Cargo;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Role;
import com.mobiauto.model.Usuario;
import com.mobiauto.service.RevendaService;
import com.mobiauto.service.RoleService;
import com.mobiauto.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CadastrarEntidadesJob implements CommandLineRunner {

    private final RoleService roleService;
    private final UsuarioService usuarioService;

    private final RevendaService revendaService;

    @Override
    public void run(String... args) throws Exception {
        inserirRolesNoBanco();
        inserirRevenda();
        inserirAdminNoBanco();
    }

    public void inserirRolesNoBanco() {
        roleService.save(Role.builder().id(1L).name(NivelAcessoConfig.NIVEL_ADMINISTRADOR).build());
        roleService.save(Role.builder().id(2L).name(NivelAcessoConfig.NIVEL_PROPRIETARIO).build());
        roleService.save(Role.builder().id(3L).name(NivelAcessoConfig.NIVEL_GERENTE).build());
        roleService.save(Role.builder().id(4L).name(NivelAcessoConfig.NIVEL_ASSISTENTE).build());
    }

    public void inserirRevenda() {
        var revenda = Revenda.builder()
                .cnpj("teste")
                .nomeSocial("teste")
                .build();

        revendaService.save(revenda);
    }

    public void inserirAdminNoBanco() {
        var revenda = Revenda.builder()
                .id(1L)
                .build();

        usuarioService.save(Usuario.builder()
                .nome("Administrador")
                .email("administrador@email.com")
                .senha("8080")
                .cargo(Cargo.ADMINISTRADOR)
                .lojaAssociada(revenda)
                .build());
    }
}
