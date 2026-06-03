package br.com.dgdev.sulwork.cafe.dto;

import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateItemStatusRequestDTO(
    @NotNull(message = "Status do item é obrigatório.")
    ItemStatus status
) {
}
