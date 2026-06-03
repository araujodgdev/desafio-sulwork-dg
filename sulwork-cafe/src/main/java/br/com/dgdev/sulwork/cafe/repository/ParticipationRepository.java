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
	
}
















