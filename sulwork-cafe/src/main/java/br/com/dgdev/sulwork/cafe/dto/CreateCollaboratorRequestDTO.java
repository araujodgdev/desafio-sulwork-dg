package br.com.dgdev.sulwork.cafe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCollaboratorRequestDTO(
    @NotBlank(message = "Nome do colaborador é obrigatório.")
    @Size(max = 120, message = "O nome do colaborador deve ter no máximo 120 caracteres.")
    String name,

    @NotBlank(message = "CPF do colaborador é obrigatório.")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 números.")
    String cpf
) {
    
}
