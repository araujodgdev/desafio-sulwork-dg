package br.com.dgdev.sulwork.cafe.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.CollaboratorDTO;
import br.com.dgdev.sulwork.cafe.dto.ItemDTO;
import br.com.dgdev.sulwork.cafe.dto.ParticipationDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class ParticipationRepository {

	@PersistenceContext
	private EntityManager entityManager;

	private final ItemsRepository itemsRepository;

	public ParticipationRepository(ItemsRepository itemsRepository) {
		this.itemsRepository = itemsRepository;
	}

	public Optional<ParticipationDTO> findParticipationByParticipationId(Long participationId) {
		return findParticipationRowById(participationId)
			.map(row -> mapToParticipation(
				row,
				itemsRepository.findItemsByParticipationId(participationId)));
	}

	public Long insertNewParticipation(Long breakfastId, Long collaboratorId) {
		String sql = """
				INSERT INTO participations (breakfast_id, collaborator_id)
				VALUES (:breakfastId, :collaboratorId)
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastId", breakfastId);
		query.setParameter("collaboratorId", collaboratorId);
		query.executeUpdate();

		Number result = (Number) entityManager.createNativeQuery("SELECT MAX(id) FROM participations").getSingleResult();
		return result.longValue();
	}

	public int deleteParticipation(Long participationId) {
		String sql = """
				DELETE FROM participations
				WHERE id = :participationId
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("participationId", participationId);
		return query.executeUpdate();
	}

	public Optional<ParticipationDTO> findParticipationByBreakfastIdAndCollaboratorId(Long breakfastId, Long collaboratorId) {
		String sql = """
				SELECT p.id, p.breakfast_id, p.collaborator_id, c.id, c.name, c.cpf
				FROM participations p
				INNER JOIN collaborators c ON p.collaborator_id = c.id
				WHERE p.breakfast_id = :breakfastId AND p.collaborator_id = :collaboratorId
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastId", breakfastId);
		query.setParameter("collaboratorId", collaboratorId);

		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		return results.stream()
			.findFirst()
			.map(row -> {
				Long participationId = ((Number) row[0]).longValue();
				return mapToParticipation(row, itemsRepository.findItemsByParticipationId(participationId));
			});
	}

	public List<ParticipationDTO> findParticipationsByBreakfastId(Long breakfastId) {
		String sql = """
				SELECT p.id, p.breakfast_id, p.collaborator_id, c.id, c.name, c.cpf
				FROM participations p
				INNER JOIN collaborators c ON p.collaborator_id = c.id
				WHERE p.breakfast_id = :breakfastId
				ORDER BY p.id
				""";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastId", breakfastId);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();

		Map<Long, List<ItemDTO>> itemsByParticipationId = itemsRepository.findItemsByBreakfastId(breakfastId).stream()
			.collect(Collectors.groupingBy(ItemDTO::participationId));

		return results.stream()
			.map(row -> {
				Long participationId = ((Number) row[0]).longValue();
				return mapToParticipation(
					row,
					itemsByParticipationId.getOrDefault(participationId, List.of()));
			})
			.collect(Collectors.toUnmodifiableList());
	}

	private Optional<Object[]> findParticipationRowById(Long participationId) {
		String sql = """
				SELECT p.id, p.breakfast_id, p.collaborator_id, c.id, c.name, c.cpf
				FROM participations p
				INNER JOIN collaborators c ON p.collaborator_id = c.id
				WHERE p.id = :participationId
				""";
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("participationId", participationId);
		@SuppressWarnings("unchecked")
		List<Object[]> results = query.getResultList();
		return results.stream().findFirst();
	}

	private ParticipationDTO mapToParticipation(Object[] row, List<ItemDTO> items) {
		return new ParticipationDTO(
			((Number) row[0]).longValue(),
			((Number) row[1]).longValue(),
			((Number) row[2]).longValue(),
			new CollaboratorDTO(((Number) row[3]).longValue(), (String) row[4], (String) row[5]),
			items
		);
	}
}
