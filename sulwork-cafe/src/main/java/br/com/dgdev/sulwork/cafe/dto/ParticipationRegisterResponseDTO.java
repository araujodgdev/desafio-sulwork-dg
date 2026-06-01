package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDate;
import java.util.List;

public record ParticipationRegisterResponseDTO(
		Long collaboratorId,
		String collaboratorName,
		String cpf,
		LocalDate breakfastDate,
		List<ItemBreakfastResponseDTO> items
		
		) {

}
