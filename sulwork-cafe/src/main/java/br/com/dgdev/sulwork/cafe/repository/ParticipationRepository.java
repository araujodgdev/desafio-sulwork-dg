package br.com.dgdev.sulwork.cafe.repository;

import java.time.LocalDate;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class ParticipationRepository {
	
	@PersistenceContext
	private EntityManager entityManager;
	
	
	
	public boolean collaboratorAlreadyExists(String cpf) {
		String sql = """
				SELECT COUNT(*)
				FROM collaborators
				WHERE cpf = :cpf
				""";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("cpf", cpf);
		
		Number result = (Number) query.getSingleResult();
		
		return result.longValue() > 0;
		
	};
	
	public boolean itemAlreadyExistsByDate(String itemName, LocalDate breakfastDate) {
		String sql = """
				SELECT COUNT(*)
				FROM breakfast_items
				WHERE LOWER(itemName) = LOWER(:itemName)
				AND breakfast_date = :breakfastDate
				""";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("breakfastDate", breakfastDate);
		query.setParameter("itemName", itemName);
		
		Number result = (Number) query.getSingleResult();
		
		return result.longValue() > 0;
	};
	
	public Long insertNewCollaborator(String collaboratorName, String collaboratorCPF) {
		String sql = """
				INSERT INTO collaborators (name, cpf)
				VALUES (:name, :cpf)
				RETURNING id
				""";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("name", collaboratorName);
		query.setParameter("cpf", collaboratorCPF);
		
		Number result = (Number) query.getSingleResult();
		
		return result.longValue();
	};
	
	public Long insertNewBreakfastItem(String itemName, LocalDate breakfastDate, Long collaboratorId, ItemStatus status) {
		String sql = """
				INSERT INTO breakfast_items (item_name, breakfast_date, collaborator_id, item_status)
				VALUES (:name, :date, :collaborator, :status)
				RETURNING id
				""";
		
		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("name", itemName);
		query.setParameter("date", breakfastDate);
		query.setParameter("collaborator", collaboratorId);
		query.setParameter("status", status.name());
		
		Number result = (Number) query.getSingleResult()
				;
		return result.longValue();
	};
	
}
















