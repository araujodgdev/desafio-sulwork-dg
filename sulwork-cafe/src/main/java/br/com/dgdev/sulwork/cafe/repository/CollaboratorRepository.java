package br.com.dgdev.sulwork.cafe.repository;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CollaboratorRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    public Long insertNewCollaborator(String name, String cpf) {
        String sql = """
            INSERT INTO collaborators (name, cpf)
            VALUES (:name, :cpf)
            RETURNING id
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("name", name);
        query.setParameter("cpf", cpf);

        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }
}
