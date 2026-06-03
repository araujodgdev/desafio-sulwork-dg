package br.com.dgdev.sulwork.cafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@Transactional
class ParticipationServiceTest {

	@Autowired
	private ParticipationService participationService;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void rejectsDuplicateParticipationForSameCollaboratorAndBreakfast() {
		var breakfast = testDataFactory.createBreakfast(testDataFactory.uniqueFutureDate());
		String cpf = testDataFactory.uniqueCpf();

		participationService.insertNewParticipation(breakfast.id(), "João Silva", cpf);

		var exception = assertThrows(
			IllegalArgumentException.class,
			() -> participationService.insertNewParticipation(breakfast.id(), "João Silva", cpf)
		);

		assertEquals("Participação já existe!", exception.getMessage());
	}

	@Test
	void rejectsDuplicatedItemInSameBreakfastAcrossDifferentParticipations() {
		var breakfast = testDataFactory.createBreakfast(testDataFactory.uniqueFutureDate());
		Long firstParticipationId = participationService.insertNewParticipation(
			breakfast.id(),
			"Maria Souza",
			testDataFactory.uniqueCpf()
		);
		Long secondParticipationId = participationService.insertNewParticipation(
			breakfast.id(),
			"Pedro Lima",
			testDataFactory.uniqueCpf()
		);

		participationService.insertNewItem("Queijo", firstParticipationId);

		var exception = assertThrows(
			IllegalArgumentException.class,
			() -> participationService.insertNewItem(" queijo ", secondParticipationId)
		);

		assertEquals("Item já cadastrado!", exception.getMessage());
	}
}
