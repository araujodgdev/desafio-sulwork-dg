package br.com.dgdev.sulwork.cafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void returnsCleanNotFoundErrorWhenUpdatingMissingItem() throws Exception {
		mockMvc.perform(put("/items/999999")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"name": "Bolo"
						}
						"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Item não encontrado!"))
			.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void returnsCleanValidationErrorForInvalidStatusPayload() throws Exception {
		var item = testDataFactory.createItem(testDataFactory.uniqueFutureDate(), "Café");

		mockMvc.perform(patch("/items/" + item.id() + "/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"status": null
						}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Dados inválidos."))
			.andExpect(jsonPath("$.status").value(400));
	}
}
