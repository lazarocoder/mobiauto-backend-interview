package com.mobiauto.controller;


import com.mobiauto.config.NivelAcessoConfig;
import com.mobiauto.model.Revenda;
import com.mobiauto.service.RevendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('" + NivelAcessoConfig.NIVEL_ADMINISTRADOR + "')")
@RequestMapping(value = "api/v1/revendas", produces = {"application/json"})
@RequiredArgsConstructor
@Tag(name = "Revendas")
@ApiResponses({
        @ApiResponse(responseCode = "200", description = "Operação executada com sucesso."),
        @ApiResponse(responseCode = "400", description = "Possui parâmetro inválido."),
        @ApiResponse(responseCode = "401", description = "Usuário ou senha invalidos na autenticação."),
        @ApiResponse(responseCode = "403", description = "Usuário não tem permissão para acessar."),
        @ApiResponse(responseCode = "404", description = "A revenda não existe atualmente.")
})
public class RevendaController {

    private final RevendaService service;

    @Operation(summary = "Busca todas as revendedoras que estão cadastradas atualmente.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @GetMapping
    public ResponseEntity<Object> buscarRevendas() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Busca uma revendedora pelo id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @GetMapping("/{id}")
    public ResponseEntity<Object> buscarRevendaPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Cadastra uma nova revendedora.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PostMapping("/cadastrar")
    public ResponseEntity<Object> cadastrarRevenda(@RequestBody @Validated Revenda revenda) {
        return ResponseEntity.ok(service.save(revenda));
    }

    @Operation(summary = "Edita uma revendedora.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @PutMapping("/editar/{id}")
    public ResponseEntity<Object> editarRevenda(@PathVariable Long id, @RequestBody @Validated Revenda revenda) {
        return ResponseEntity.ok(service.update(id, revenda));
    }

    @Operation(summary = "Deleta uma revendedora pelo id.", description = NivelAcessoConfig.NIVEL_ADMINISTRADOR)
    @DeleteMapping("/deletar/{id}")
    public ResponseEntity<Object> deletarRevendaPorId(@PathVariable Long id) {
            service.delete(id);
            return ResponseEntity.ok().body("Revenda excluída com sucesso.");
    }

}
