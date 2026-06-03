package br.com.dgdev.sulwork.cafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.dgdev.sulwork.cafe.exception.ResourceNotFoundException;
import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@Transactional
class BreakfastServiceTest {

	@Autowired
	private BreakfastService breakfastService;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void trimsLocationWhenCreatingBreakfast() {
		var breakfastDate = testDataFactory.uniqueFutureDate();

		Long breakfastId = breakfastService.insertNewBreakfast(
			breakfastDate,
			LocalTime.of(8, 30),
			"  Sala de reunião  "
		);

		var breakfast = breakfastService.findBreakfastById(breakfastId);
		assertEquals("Sala de reunião", breakfast.location());
	}

	@Test
	void rejectsDuplicateBreakfastDate() {
		var breakfastDate = testDataFactory.uniqueFutureDate();
		breakfastService.insertNewBreakfast(breakfastDate, LocalTime.of(8, 30), "Sala 1");

		var exception = assertThrows(
			IllegalArgumentException.class,
			() -> breakfastService.insertNewBreakfast(breakfastDate, LocalTime.of(9, 0), "Sala 2")
		);

		assertEquals("Café da manhã já existe para a data informada!", exception.getMessage());
	}

	@Test
	void throwsNotFoundWhenDeletingMissingBreakfast() {
		var exception = assertThrows(
			ResourceNotFoundException.class,
			() -> breakfastService.deleteBreakfast(999999L)
		);

		assertEquals("Café da manhã não encontrado!", exception.getMessage());
	}
}
