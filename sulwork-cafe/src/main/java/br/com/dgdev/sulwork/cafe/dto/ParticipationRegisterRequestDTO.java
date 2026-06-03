package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ParticipationRegisterRequestDTO(
		
		@NotBlank(message = "Nome do colaborador é Obridatório!")
		@Size(max = 120, message = "O nome do colaborador deve ter no máximo 120 caracteres")
		String collaboratorName,
		
		@NotBlank(message = "CPF é obrigatório!")
		@Pattern(regexp = "\\d{11}", message = "O CPF deve ter exatamente 11 números.")
		@CPF(message = "CPF Inválido")
		String cpf,
		
		@NotNull(message = "Data do café da manhã é obrigatória!")
		@Future(message = "A Data do café da manhã deve ser maior que a data atual!")
		LocalDate breakfastDate,
		
		@NotEmpty(message = "Informe ao menos 1 item para o café da manhã.")
		List<
			@NotBlank(message = "Item do café da manhã não pode estar em branco")
			@Size(max = 120, message = "O nome do Item deve ter no máximo 120 caracteres.")
			String
		> items
		
		) {

}
