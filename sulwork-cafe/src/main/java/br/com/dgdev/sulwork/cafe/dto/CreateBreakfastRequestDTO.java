package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateBreakfastRequestDTO(
    @NotNull(message = "Data do café da manhã é obrigatória.")
    @Future(message = "A data do café da manhã deve ser maior que a data atual.")
    LocalDate breakfastDate,

    @NotNull(message = "Horário do café da manhã é obrigatório.")
    LocalTime breakfastTime,

    @NotBlank(message = "Local do café da manhã é obrigatório.")
    @Size(max = 120, message = "O local do café da manhã deve ter no máximo 120 caracteres.")
    String location
) {
    
}
