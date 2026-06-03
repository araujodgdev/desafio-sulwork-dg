package br.com.dgdev.sulwork.cafe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
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

}
