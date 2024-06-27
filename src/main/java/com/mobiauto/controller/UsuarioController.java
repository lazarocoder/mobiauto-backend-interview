package com.mobiauto.controller;


import com.mobiauto.config.NivelAcessoConfig;
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
        return ResponseEntity.ok(service.buscarUsuariosDaRevenda(userPrincipal));
    }

    @Operation(summary = "Busca um usuário pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarUsuarioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cadastrar um novo usuário.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarUsuario(@RequestBody @Validated Usuario usuario) {
        return ResponseEntity.ok(service.save(usuario));
    }

    @Operation(summary = "Cadastra um usuário na revendedora do usuário autenticado.", description = NivelAcessoConfig.NIVEL_GERENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PostMapping("/cadastrar/revenda")
    public ResponseEntity<Object> cadastrarUsuarioEmRevenda(@RequestBody @Validated Usuario usuario, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(service.cadastrarUsuarioEmRevenda(usuario, userPrincipal));
    }

    @Operation(summary = "Edita um usuário pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarUsuario(@PathVariable Long id, @RequestBody @Validated Usuario usuario) {
        return ResponseEntity.ok(service.update(id, usuario));
    }

    @Operation(summary = "Edita um usuário pelo seu id, validando se o usuário autenticado está na mesma revendedora.", description = NivelAcessoConfig.NIVEL_PROPRIETARIO)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_PROPRIETARIO + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarUsuarioEmRevenda(@PathVariable Long id, @RequestBody @Validated Usuario usuario, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(service.editarUsuarioEmRevenda(id, usuario, userPrincipal));
    }

    @Operation(summary = "Deleta um usuário pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarUsuarioPorId(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body("Usuário excluído com sucesso.");
    }

}
