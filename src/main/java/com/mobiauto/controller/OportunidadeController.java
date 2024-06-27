package com.mobiauto.controller;


import com.mobiauto.config.NivelAcessoConfig;
import com.mobiauto.dto.CadastroOportunidadeDto;
import com.mobiauto.model.Oportunidade;
import com.mobiauto.security.UserPrincipal;
import com.mobiauto.service.OportunidadeService;
import com.mobiauto.service.RevendaService;
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
@RequestMapping(value = "api/v1/oportunidades", produces = {"application/json"})
@RequiredArgsConstructor
@Tag(name = "Oportunidades")
@ApiResponses({@ApiResponse(responseCode = "200", description = "Operação executada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Possui parâmetro inválido."),
        @ApiResponse(responseCode = "401", description = "Usuário ou senha inválidos na autenticação."),
        @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar."),
        @ApiResponse(responseCode = "404", description = "A oportunidade não existe.")})
public class OportunidadeController {

    private final OportunidadeService service;
    private final UsuarioService usuarioService;

    private final RevendaService revendaService;

    @Operation(summary = "Busca todas as oportunidades cadastradas atualmente.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping
    public ResponseEntity<Object> buscarOportunidades() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Busca todas as oportunidades associadas à revendedora do usuário autenticado.", description = NivelAcessoConfig.NIVEL_ASSISTENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @GetMapping("/revenda")
    public ResponseEntity<Object> buscarOportunidadesDaRevenda(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        return  ResponseEntity.ok(service.buscarOportunidadesDaRevenda(userPrincipal));
    }

    @Operation(summary = "Busca uma oportunidade pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarOportunidadePorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cadastra uma nova oportunidade.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarOportunidade(@RequestBody @Validated CadastroOportunidadeDto cadastroOportunidadeDto) {
        return ResponseEntity.ok(service.save(cadastroOportunidadeDto));
    }

    @Operation(summary = "Cadastra uma oportunidade na mesma revendedora para um usuário autenticado, com distribuição ao assistente mais ocioso no momento.", description = NivelAcessoConfig.NIVEL_ASSISTENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @PostMapping("/atender")
    public ResponseEntity<Object> atenderOportunidade(@RequestBody @Validated CadastroOportunidadeDto cadastroOportunidadeDto, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(service.atender(cadastroOportunidadeDto, userPrincipal));
    }

    @Operation(summary = "Edita uma oportunidade pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarOportunidade(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade) {
        return ResponseEntity.ok(service.update(id, oportunidade));
    }

    @Operation(summary = "Edita uma oportunidade pelo seu id, e valida se o usuário autenticado pertence à uma mesma revendedora.", description = NivelAcessoConfig.NIVEL_GERENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_GERENTE + "')")
    @PutMapping("/editar/revenda/{id}")
    public ResponseEntity<Object> editarOportunidadeEmRevenda(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(service.editarOportunidadeEmRevenda(id, oportunidade, userPrincipal));
    }

    @Operation(summary = "Edita uma oportunidade pelo seu id, e valida se o usuário autenticado está relacionado à oportunidade no momento.", description = NivelAcessoConfig.NIVEL_ASSISTENTE)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ASSISTENTE + "')")
    @PutMapping("/editar/associado/{id}")
    public ResponseEntity<Object> editarOportunidadeAssociada(@PathVariable Long id, @RequestBody @Validated Oportunidade oportunidade, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return ResponseEntity.ok(service.editarOportunidadeAssociada(id, oportunidade, userPrincipal));
    }

    @Operation(summary = "Deleta uma oportunidade pelo seu id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarOportunidadePorId(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok().body("Oportunidade realizada com sucesso.");
    }

}
