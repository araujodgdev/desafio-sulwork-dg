package br.com.dgdev.sulwork.cafe.testsupport;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TestDataFactory {

	private static final AtomicInteger DATE_OFFSET = new AtomicInteger(200);
	private static final AtomicInteger CPF_SEQUENCE = new AtomicInteger(200);

	private final JdbcTemplate jdbcTemplate;

	public TestDataFactory(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public LocalDate uniqueFutureDate() {
		return LocalDate.now().plusDays(DATE_OFFSET.incrementAndGet());
	}

	public LocalDate uniquePastDate() {
		return LocalDate.now().minusDays(DATE_OFFSET.incrementAndGet());
	}

	public TestBreakfast createBreakfast(LocalDate breakfastDate) {
		String suffix = uniqueSuffix();
		jdbcTemplate.update(
			"INSERT INTO breakfasts (breakfast_date, breakfast_time, location) VALUES (?, ?, ?)",
			Date.valueOf(breakfastDate),
			Time.valueOf(LocalTime.of(8, 30)),
			"Sala " + suffix
		);
		Long breakfastId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM breakfasts", Long.class);
		return new TestBreakfast(breakfastId, breakfastDate);
	}

	public TestParticipation createParticipation(Long breakfastId) {
		Long collaboratorId = createCollaboratorId();
		jdbcTemplate.update(
			"INSERT INTO participations (breakfast_id, collaborator_id) VALUES (?, ?)",
			breakfastId,
			collaboratorId
		);
		Long participationId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM participations", Long.class);
		return new TestParticipation(participationId, breakfastId, collaboratorId);
	}

	public TestItem createItem(LocalDate breakfastDate, String itemName) {
		TestBreakfast breakfast = createBreakfast(breakfastDate);
		TestParticipation participation = createParticipation(breakfast.id());
		return createItem(breakfast.id(), participation.id(), itemName);
	}

	public TestItem createItem(Long breakfastId, Long participationId, String itemName) {
		jdbcTemplate.update(
			"INSERT INTO breakfast_items (breakfast_id, participation_id, item_name) VALUES (?, ?, ?)",
			breakfastId,
			participationId,
			itemName
		);
		Long itemId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM breakfast_items", Long.class);
		return new TestItem(itemId, breakfastId, participationId, itemName);
	}

	public String uniqueCpf() {
		String base = String.format("%09d", CPF_SEQUENCE.incrementAndGet());
		int firstDigit = calculateCpfDigit(base, 10);
		int secondDigit = calculateCpfDigit(base + firstDigit, 11);
		return base + firstDigit + secondDigit;
	}

	private Long createCollaboratorId() {
		String suffix = uniqueSuffix();
		jdbcTemplate.update(
			"INSERT INTO collaborators (name, cpf) VALUES (?, ?)",
			"Colaborador " + suffix,
			uniqueCpf()
		);
		return jdbcTemplate.queryForObject("SELECT MAX(id) FROM collaborators", Long.class);
	}

	private static String uniqueSuffix() {
		return String.valueOf(System.nanoTime());
	}

	private static int calculateCpfDigit(String digits, int initialWeight) {
		int sum = 0;
		for (int index = 0; index < digits.length(); index++) {
			sum += Character.getNumericValue(digits.charAt(index)) * (initialWeight - index);
		}

		int result = 11 - (sum % 11);
		return result > 9 ? 0 : result;
	}

	public record TestBreakfast(Long id, LocalDate date) {
	}

	public record TestParticipation(Long id, Long breakfastId, Long collaboratorId) {
	}

	public record TestItem(Long id, Long breakfastId, Long participationId, String name) {
	}
}
