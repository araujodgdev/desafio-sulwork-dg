package br.com.dgdev.sulwork.cafe.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@Transactional
class ItemsRepositoryTest {

	@Autowired
	private ItemsRepository itemsRepository;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void findsExistingItemIgnoringCaseAndSpaces() {
		var item = testDataFactory.createItem(testDataFactory.uniqueFutureDate(), "Queijo Coalho");

		assertTrue(itemsRepository.itemExistsInBreakfast(item.breakfastId(), " queijo coalho "));
		assertTrue(itemsRepository.itemExistsInParticipation(item.participationId(), " QUEIJO COALHO "));
		assertTrue(itemsRepository.findItemByItemNameAndBreakfastId("queijo coalho", item.breakfastId()).isPresent());
	}

	@Test
	void expiresOnlyPendingItemsFromPastBreakfasts() {
		var pastItem = testDataFactory.createItem(testDataFactory.uniquePastDate(), "Pão");
		var futureItem = testDataFactory.createItem(testDataFactory.uniqueFutureDate(), "Suco");

		int updatedRows = itemsRepository.updatePendingItemsFromPastBreakfasts(LocalDate.now());

		assertTrue(updatedRows >= 1);
		assertEquals(ItemStatus.NAO_TROUXE, itemsRepository.findItemById(pastItem.id()).orElseThrow().status());
		assertEquals(ItemStatus.PENDENTE, itemsRepository.findItemById(futureItem.id()).orElseThrow().status());
	}
}
