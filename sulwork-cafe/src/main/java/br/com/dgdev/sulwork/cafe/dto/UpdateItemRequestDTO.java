package br.com.dgdev.sulwork.cafe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateItemRequestDTO(
    @NotBlank(message = "Nome do item é obrigatório.")
    @Size(max = 120, message = "O nome do item deve ter no máximo 120 caracteres.")
    String name
) {
}
