package br.com.dgdev.sulwork.cafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
class ParticipationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void createsParticipationAndItem() throws Exception {
		var breakfast = testDataFactory.createBreakfast(testDataFactory.uniqueFutureDate());

		var participationResult = mockMvc.perform(post("/participations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"breakfastId": %d,
							"name": "Ana Recife",
							"cpf": "%s"
						}
						""".formatted(breakfast.id(), testDataFactory.uniqueCpf())))
			.andExpect(status().isCreated())
			.andReturn();

		Long participationId = Long.valueOf(participationResult.getResponse().getContentAsString());

		mockMvc.perform(post("/participations/" + participationId + "/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"name": "Suco de acerola"
						}
						"""))
			.andExpect(status().isCreated());

		mockMvc.perform(get("/breakfasts/" + breakfast.id()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.participations[0].collaborator.name").value("Ana Recife"))
			.andExpect(jsonPath("$.participations[0].items[0].name").value("Suco de acerola"))
			.andExpect(jsonPath("$.participations[0].items[0].status").value("PENDENTE"));
	}
}
