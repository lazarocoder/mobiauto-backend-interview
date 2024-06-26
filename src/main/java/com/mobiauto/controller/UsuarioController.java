package com.mobiauto.controller;


import com.mobiauto.config.NivelAcessoConfig;
import com.mobiauto.model.Revenda;
import com.mobiauto.model.Usuario;
import com.mobiauto.security.UserPrincipal;
import com.mobiauto.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(value = "api/v1/usuarios", produces = {"application/json"})
@RequiredArgsConstructor
@Tag(name = "Usuarios")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação executada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Possui parâmetro foi inválido."),
        @ApiResponse(responseCode = "401", description = "Usuário ou senha incorretos na autenticação."),
        @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para usar essa rota."),
        @ApiResponse(responseCode = "404", description = "O usuário não existe.")
})
public class UsuarioController {

    private final UsuarioService service;

    @Operation(summary = "Busca todos os usuários cadastrados.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping
    public ResponseEntity<Object> buscarUsuarios() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Busca todos os usuários cadastrados na revendedora do usuário autenticado.", description = NivelAcessoConfig.NIVEL_GERENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @GetMapping("/revenda")
    public ResponseEntity<Object> buscarUsuariosDaRevenda(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Revenda revenda = getUsuarioAutenticado(userPrincipal).getLojaAssociada();
        if (revenda == null) {
            return ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada para que possa realizar a busca.");
        }
        return ResponseEntity.ok(service.findAllInRevenda(revenda.getId()));
    }

    @Operation(summary = "Busca um usuário pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = service.findById(id);
        return (usuario != null) ? ResponseEntity.ok(usuario) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Cadastrar um novo usuário.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarUsuario(@RequestBody @Validated Usuario usuario) {
        ResponseEntity<Object> erro = validarUsuario(true, usuario, null);
        if (erro != null) return erro;
        return ResponseEntity.ok(service.save(usuario));
    }

    @Operation(summary = "Cadastra um usuário na revendedora do usuário autenticado.", description = NivelAcessoConfig.NIVEL_GERENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PostMapping("/cadastrar/revenda")
    public ResponseEntity<Object> cadastrarUsuarioEmRevenda(@RequestBody @Validated Usuario usuario, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseEntity<Object> erro = validarUsuario(true, usuario, null);
        if (erro != null) return erro;

        Usuario usuarioAutenticado = getUsuarioAutenticado(userPrincipal);
        if (usuarioAutenticado.getLojaAssociada() == null) {
            return ResponseEntity.badRequest().body("O usuário precisa ter uma loja que seja associada ao mesmo para realizar o cadastro.");
        }

        usuario.setLojaAssociada(usuarioAutenticado.getLojaAssociada());
        return ResponseEntity.ok(service.save(usuario));
    }

    @Operation(summary = "Edita um usuário pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarUsuario(@PathVariable Long id, @RequestBody @Validated Usuario usuario) {
        ResponseEntity<Object> erro = validarUsuario(false, usuario, id);
        if (erro != null) return erro;
        return ResponseEntity.ok(service.update(id, usuario));
    }

    @Operation(summary = "Edita um usuário pelo seu id, validando se o usuário autenticado está na mesma revendedora.", description = NivelAcessoConfig.NIVEL_PROPRIETARIO)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_PROPRIETARIO + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarUsuarioEmRevenda(@PathVariable Long id, @RequestBody @Validated Usuario usuario, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ResponseEntity<Object> erro = validarUsuario(false, usuario, id);
        if (erro != null) return erro;

        Usuario usuarioAutenticado = getUsuarioAutenticado(userPrincipal);
        Long idRevendaUsuarioAutenticado = usuarioAutenticado.getLojaAssociada().getId();
        Long idRevendaUsuarioNovo = service.findById(id).getLojaAssociada().getId();

        if (!Objects.equals(idRevendaUsuarioAutenticado, idRevendaUsuarioNovo)) {
            return ResponseEntity.badRequest().body("O usuário precisa ter uma loja associada correspondente à loja do usuário a ser editado.");
        }

        return ResponseEntity.ok(service.update(id, usuario));
    }

    @Operation(summary = "Deleta um usuário pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarUsuarioPorId(@PathVariable Long id) {
        if (service.findById(id) != null) {
            service.delete(id);
            return ResponseEntity.ok().body("Usuário excluído com sucesso.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private Usuario getUsuarioAutenticado(UserPrincipal userPrincipal) {
        return service.findByEmail(userPrincipal.getUsername());
    }

    private ResponseEntity<Object> validarUsuario(boolean isCadastro, Usuario usuario, Long id) {
        if (isCadastro && usuario.getId() != null) {
            return ResponseEntity.badRequest().body("O parâmetro 'id' não pode ser informado em cadastro.");
        }

        Usuario usuarioPorEmail = service.findByEmail(usuario.getEmail());

        if (isCadastro && usuarioPorEmail != null) {
            return ResponseEntity.badRequest().body("O email informado já possui cadastro.");
        }

        if (!isCadastro) {
            if (usuarioPorEmail != null && !Objects.equals(id, usuarioPorEmail.getId())) {
                return ResponseEntity.badRequest().body("O email informado já possui cadastro.");
            }
            if (service.findById(id) == null) {
                return ResponseEntity.notFound().build();
            }
        }

        return null;
    }
}
