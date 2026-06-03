package br.com.dgdev.sulwork.cafe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import br.com.dgdev.sulwork.cafe.exception.ResourceNotFoundException;
import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@Transactional
class ItemsServiceTest {

	@Autowired
	private ItemsService itemsService;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void rejectsDuplicatedItemNameWhenUpdatingItem() {
		var breakfast = testDataFactory.createBreakfast(testDataFactory.uniqueFutureDate());
		var participation = testDataFactory.createParticipation(breakfast.id());
		testDataFactory.createItem(breakfast.id(), participation.id(), "Pão");
		var itemToUpdate = testDataFactory.createItem(breakfast.id(), participation.id(), "Bolo");

		var exception = assertThrows(
			IllegalArgumentException.class,
			() -> itemsService.updateItem(itemToUpdate.id(), " pão ")
		);

		assertEquals("Item já cadastrado!", exception.getMessage());
	}

	@Test
	void blocksStatusUpdateOutsideBreakfastDate() {
		var item = testDataFactory.createItem(testDataFactory.uniqueFutureDate(), "Suco");

		var exception = assertThrows(
			IllegalArgumentException.class,
			() -> itemsService.updateItemStatus(item.id(), ItemStatus.TROUXE)
		);

		assertEquals("Status do item só pode ser atualizado no dia do café.", exception.getMessage());
	}

	@Test
	void throwsNotFoundWhenDeletingMissingItem() {
		var exception = assertThrows(
			ResourceNotFoundException.class,
			() -> itemsService.deleteItem(999999L)
		);

		assertEquals("Item não encontrado!", exception.getMessage());
	}
}
