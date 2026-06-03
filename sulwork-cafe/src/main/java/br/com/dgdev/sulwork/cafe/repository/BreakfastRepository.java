package br.com.dgdev.sulwork.cafe.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.BreakfastDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BreakfastRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	public Optional<BreakfastDTO> findBreakfastById(Long id) {
		String sql = """
				SELECT * FROM breakfasts WHERE id = :id
				""";
				
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("id", id);
		
		List<Object[]> results = query.getResultList();
		
		return results.stream()
            .findFirst()
			.map(this::mapToBreakfastDTO);
	}

    public Optional<BreakfastDTO> findBreakfastByDate(LocalDate breakfastDate) {
        String sql = """
                SELECT * FROM breakfasts WHERE breakfast_date = :breakfastDate
                """;
                
                Query query = entityManager.createNativeQuery(sql);
                query.setParameter("breakfastDate", breakfastDate);
                List<Object[]> results = query.getResultList();
                return results.stream()
                    .findFirst()
                    .map(this::mapToBreakfastDTO);
    }


    public List<BreakfastDTO> findAllBreakfasts() {
        String sql = """
                SELECT * FROM breakfasts
                """;
                
                Query query = entityManager.createNativeQuery(sql);
                List<Object[]> results = query.getResultList();
                return results.stream()
                    .map(this::mapToBreakfastDTO)
                    .collect(Collectors.toUnmodifiableList());
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


	private BreakfastDTO mapToBreakfastDTO(Object[] result) {
		return new BreakfastDTO(
			(Long) result[0],
			(LocalDate) result[1],
			(LocalTime) result[2],
			(String) result[3],
			((Timestamp) result[4]).toLocalDateTime()
		);
	}
}
