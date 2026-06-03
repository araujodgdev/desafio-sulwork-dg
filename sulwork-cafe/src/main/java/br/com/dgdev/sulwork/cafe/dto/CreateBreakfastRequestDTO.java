package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateBreakfastRequestDTO(
    LocalDate breakfastDate,
    LocalTime breakfastTime,
    String location
) {
    
}
