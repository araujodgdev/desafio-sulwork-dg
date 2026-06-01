package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDate;
import java.util.List;

public record ParticipationRegisterResponseDTO(
		Long colaboratorId,
		String colaboratorName,
		String cpf,
		LocalDate coffeeDate,
		List<ItemCoffeeResponseDTO> items
		
		) {

}
