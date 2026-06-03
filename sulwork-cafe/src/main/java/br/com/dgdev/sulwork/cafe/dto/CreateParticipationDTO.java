package br.com.dgdev.sulwork.cafe.dto;

import jakarta.validation.constraints.NotNull;

public record CreateParticipationDTO(
    @NotNull(message = "ID do café da manhã é obrigatório.")
    Long breakfastId,

    @NotNull(message = "Nome do colaborador é obrigatório.")
    String name,

    @NotNull(message = "CPF do colaborador é obrigatório.")
    String cpf
) {
}