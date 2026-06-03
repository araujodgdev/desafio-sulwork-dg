package br.com.dgdev.sulwork.cafe.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import br.com.dgdev.sulwork.cafe.testsupport.TestDataFactory;

@SpringBootTest
@AutoConfigureMockMvc
class BreakfastControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TestDataFactory testDataFactory;

	@Test
	void createsBreakfastAndReturnsItById() throws Exception {
		LocalDate breakfastDate = testDataFactory.uniqueFutureDate();

		var result = mockMvc.perform(post("/breakfasts")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"breakfastDate": "%s",
							"breakfastTime": "08:30",
							"location": "  Sala principal  "
						}
						""".formatted(breakfastDate)))
			.andExpect(status().isCreated())
			.andReturn();

		Long breakfastId = Long.valueOf(result.getResponse().getContentAsString());

		mockMvc.perform(get("/breakfasts/" + breakfastId))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.breakfastDate").value(breakfastDate.toString()))
			.andExpect(jsonPath("$.breakfastTime").value("08:30:00"))
			.andExpect(jsonPath("$.location").value("Sala principal"));
	}
}
