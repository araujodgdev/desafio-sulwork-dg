package br.com.dgdev.sulwork.cafe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SulworkCafeApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void returnsCleanValidationErrors() throws Exception {
		mockMvc.perform(post("/breakfasts")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"breakfastDate": null,
							"breakfastTime": null,
							"location": ""
						}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Dados inválidos."))
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.errors").isArray())
			.andExpect(jsonPath("$.errors[0]", containsString("obrigat")));
	}

	@Test
	void returnsCleanNotFoundErrors() throws Exception {
		mockMvc.perform(get("/breakfasts/999999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Café da manhã não encontrado!"))
			.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void requiresCpfWithElevenDigits() throws Exception {
		mockMvc.perform(post("/participations")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"breakfastId": 1,
							"name": "João",
							"cpf": "732.442.160-13"
						}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.errors", hasItem("CPF deve conter exatamente 11 números.")));
	}

	@Test
	void returnsNotFoundWhenAddingItemToInvalidParticipation() throws Exception {
		mockMvc.perform(post("/participations/999999/items")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"name": "Queijo"
						}
						"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Participação não encontrada!"));
	}

}
