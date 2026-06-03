package br.com.dgdev.sulwork.cafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@Transactional
class BreakfastRepositoryTest {

	@Autowired
	private BreakfastRepository breakfastRepository;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void insertsFindsUpdatesAndDeletesBreakfastUsingNativeSql() {
		var breakfastDate = testDataFactory.uniqueFutureDate();
		Long breakfastId = breakfastRepository.insertNewBreakfast(
			breakfastDate,
			LocalTime.of(8, 30),
			"Sala Recife"
		);

		var created = breakfastRepository.findBreakfastByDate(breakfastDate);

		assertTrue(created.isPresent());
		assertEquals(breakfastId, created.get().id());
		assertEquals("Sala Recife", created.get().location());

		var newDate = testDataFactory.uniqueFutureDate();
		int updatedRows = breakfastRepository.updateBreakfast(
			breakfastId,
			newDate,
			LocalTime.of(9, 0),
			"Sala Olinda"
		);

		assertEquals(1, updatedRows);
		assertTrue(breakfastRepository.findBreakfastByDateIgnoringId(newDate, breakfastId + 999).isPresent());
		assertFalse(breakfastRepository.findBreakfastByDateIgnoringId(newDate, breakfastId).isPresent());

		int deletedRows = breakfastRepository.deleteBreakfast(breakfastId);

		assertEquals(1, deletedRows);
		assertTrue(breakfastRepository.findBreakfastById(breakfastId, List.of()).isEmpty());
	}
}
