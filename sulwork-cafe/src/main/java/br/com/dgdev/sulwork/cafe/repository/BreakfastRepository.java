package br.com.dgdev.sulwork.cafe.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.BreakfastDTO;
import br.com.dgdev.sulwork.cafe.dto.ParticipationDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BreakfastRepository {

	@PersistenceContext
	private EntityManager entityManager;

	public Optional<BreakfastDTO> findBreakfastById(Long id, List<ParticipationDTO> participations) {
		return findBreakfastRowById(id)
			.map(row -> mapToBreakfastDTO(row, participations));
	}

	public Optional<BreakfastDTO> findBreakfastByDate(LocalDate breakfastDate) {
		String sql = """
				SELECT id, breakfast_date, breakfast_time, location, created_at
				FROM breakfasts
				WHERE breakfast_date = :breakfastDate
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastDate", breakfastDate);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		return results.stream()
			.findFirst()
			.map(row -> mapToBreakfastDTO(row, List.of()));
	}

	public List<Object[]> findAllBreakfastRows() {
		String sql = """
				SELECT id, breakfast_date, breakfast_time, location, created_at
				FROM breakfasts
				ORDER BY breakfast_date DESC
				""";

		Query query = entityManager.createNativeQuery(sql);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		return results;
	}

	public Optional<Object[]> findBreakfastRowById(Long id) {
		String sql = """
				SELECT id, breakfast_date, breakfast_time, location, created_at
				FROM breakfasts
				WHERE id = :id
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("id", id);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		return results.stream().findFirst();
	}

	public Long insertNewBreakfast(LocalDate breakfastDate, LocalTime breakfastTime, String location) {
		String sql = """
				INSERT INTO breakfasts (breakfast_date, breakfast_time, location)
				VALUES (:breakfastDate, :breakfastTime, :location)
				RETURNING id
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastDate", breakfastDate);
		query.setParameter("breakfastTime", breakfastTime);
		query.setParameter("location", location);

		Number result = (Number) query.getSingleResult();

		return result.longValue();
	}

	public BreakfastDTO mapToBreakfastDTO(Object[] row, List<ParticipationDTO> participations) {
		return new BreakfastDTO(
			((Number) row[0]).longValue(),
			(LocalDate) row[1],
			(LocalTime) row[2],
			(String) row[3],
			(LocalDateTime) row[4],
			participations
		);
	}
}
