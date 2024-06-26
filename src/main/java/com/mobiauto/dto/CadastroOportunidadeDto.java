package com.mobiauto.dto;

import com.mobiauto.enumerated.Status;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CadastroOportunidadeDto {

    private Status status;

    private String nomeCliente;

    private String emailCliente;

    private String telefoneCliente;

    private String marcaVeiculo;

    private String modeloVeiculo;

    private String versaoVeiculo;

    private Integer anoVeiculo;

    private LocalDate dataAtribuicao;

    private LocalDate dataConclusao;

    private String motivoConclusao;

    private Long lojaAssociadaId;

    private Long usuarioAssociadoId;
}