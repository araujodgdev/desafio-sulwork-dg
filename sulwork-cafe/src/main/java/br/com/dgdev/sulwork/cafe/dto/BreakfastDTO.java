package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record BreakfastDTO(
		Long id,
		LocalDate breakfastDate,
		LocalTime breakfastTime,
		String location,
		LocalDateTime createdDateTime
		) {
}
