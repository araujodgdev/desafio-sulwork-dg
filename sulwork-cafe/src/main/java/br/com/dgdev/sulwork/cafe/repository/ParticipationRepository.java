package br.com.dgdev.sulwork.cafe.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.ParticipationDTO;
import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class ParticipationRepository {
	
	@PersistenceContext
	private EntityManager entityManager;


	public Long insertNewParticipation(Long breakfastId, Long collaboratorId) {
		String sql = """
			INSERT INTO participations (breakfast_id, collaborator_id)
			VALUES (:breakfastId, :collaboratorId)
			RETURNING id
		""";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastId", breakfastId);
		query.setParameter("collaboratorId", collaboratorId);

		Number result = (Number) query.getSingleResult();
		return result.longValue();
	}

	public Optional<ParticipationDTO> findParticipationByBreakfastIdAndCollaboratorId(Long breakfastId, Long collaboratorId) {
		String sql = """
			SELECT * FROM participations WHERE breakfast_id = :breakfastId AND collaborator_id = :collaboratorId
		""";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastId", breakfastId);
		query.setParameter("collaboratorId", collaboratorId);

		List<Object[]> results = query.getResultList();
		return results.stream()
			.findFirst()
			.map(this::mapToParticipationDTO);
	}
	
	private ParticipationDTO mapToParticipationDTO(Object[] result) {
		return new ParticipationDTO(
			(Long) result[0],
			(Long) result[1],
			(Long) result[2]
		);
	}
}
















