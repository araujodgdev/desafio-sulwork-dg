package br.com.dgdev.sulwork.cafe;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SulworkCafeApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

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

	@Test
	void updatesItemStatusOnBreakfastDate() throws Exception {
		TestItemData data = createItemData(LocalDate.now(), "Suco");

		mockMvc.perform(patch("/items/" + data.itemId() + "/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"status": "TROUXE"
						}
						"""))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.participations[0].items[0].status").value("TROUXE"));
	}

	@Test
	void blocksItemStatusUpdateOutsideBreakfastDate() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(20), "Bolo");

		mockMvc.perform(patch("/items/" + data.itemId() + "/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"status": "NAO_TROUXE"
						}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Status do item só pode ser atualizado no dia do café."));
	}

	@Test
	void blocksPendingStatusUpdate() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(21), "Cuscuz");

		mockMvc.perform(patch("/items/" + data.itemId() + "/status")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"status": "PENDENTE"
						}
						"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Status deve ser TROUXE ou NAO_TROUXE."));
	}

	@Test
	void expiresPendingItemsFromPastBreakfastsWhenReadingBreakfast() throws Exception {
		TestItemData data = createItemData(LocalDate.now().minusDays(1), "Pão");

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.participations[0].items[0].status").value("NAO_TROUXE"));
	}

	@Test
	void updatesBreakfast() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(40), "Fruta");
		LocalDate newDate = LocalDate.now().plusDays(41);

		mockMvc.perform(put("/breakfasts/" + data.breakfastId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"breakfastDate": "%s",
							"breakfastTime": "09:15",
							"location": "Auditório"
						}
						""".formatted(newDate)))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.breakfastDate").value(newDate.toString()))
			.andExpect(jsonPath("$.location").value("Auditório"));
	}

	@Test
	void deletesBreakfast() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(42), "Tapioca");

		mockMvc.perform(delete("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isNotFound());
	}

	@Test
	void deletesParticipation() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(43), "Café");

		mockMvc.perform(delete("/participations/" + data.participationId()))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.participations.length()").value(0));
	}

	@Test
	void updatesItemName() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(44), "Mingau");

		mockMvc.perform(put("/items/" + data.itemId())
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{
							"name": "Mingau de milho"
						}
						"""))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.participations[0].items[0].name").value("Mingau de milho"));
	}

	@Test
	void deletesItem() throws Exception {
		TestItemData data = createItemData(LocalDate.now().plusDays(45), "Banana");

		mockMvc.perform(delete("/items/" + data.itemId()))
			.andExpect(status().isNoContent());

		mockMvc.perform(get("/breakfasts/" + data.breakfastId()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.participations[0].items.length()").value(0));
	}

	private TestItemData createItemData(LocalDate breakfastDate, String itemName) {
		String suffix = String.valueOf(System.nanoTime());
		jdbcTemplate.update(
			"INSERT INTO breakfasts (breakfast_date, breakfast_time, location) VALUES (?, ?, ?)",
			Date.valueOf(breakfastDate),
			Time.valueOf(LocalTime.of(8, 30)),
			"Sala " + suffix
		);
		Long breakfastId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM breakfasts", Long.class);

		jdbcTemplate.update(
			"INSERT INTO collaborators (name, cpf) VALUES (?, ?)",
			"Colaborador " + suffix,
			suffix.substring(Math.max(0, suffix.length() - 11))
		);
		Long collaboratorId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM collaborators", Long.class);

		jdbcTemplate.update(
			"INSERT INTO participations (breakfast_id, collaborator_id) VALUES (?, ?)",
			breakfastId,
			collaboratorId
		);
		Long participationId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM participations", Long.class);

		jdbcTemplate.update(
			"INSERT INTO breakfast_items (breakfast_id, participation_id, item_name) VALUES (?, ?, ?)",
			breakfastId,
			participationId,
			itemName + " " + suffix
		);
		Long itemId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM breakfast_items", Long.class);

		return new TestItemData(breakfastId, participationId, itemId);
	}

	private record TestItemData(Long breakfastId, Long participationId, Long itemId) {
	}

}
