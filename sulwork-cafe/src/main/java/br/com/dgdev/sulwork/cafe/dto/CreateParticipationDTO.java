package br.com.dgdev.sulwork.cafe.dto;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateParticipationDTO(
    @NotNull(message = "ID do café da manhã é obrigatório.")
    Long breakfastId,

    @NotNull(message = "Nome do colaborador é obrigatório.")
    @Size(max = 120, message = "O nome do colaborador deve ter no máximo 120 caracteres.")
    @NotBlank(message = "Nome do colaborador é obrigatório.")
    String name,

    @NotNull(message = "CPF do colaborador é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 números.")
    @CPF(message = "CPF inválido.")
    String cpf
) {
}
