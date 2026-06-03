package br.com.dgdev.sulwork.cafe.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.CollaboratorDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class CollaboratorRepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    public Long findOrCreateCollaborator(String name, String cpf) {
        Optional<CollaboratorDTO> existingCollaborator = findCollaboratorByCpf(cpf);
        if (existingCollaborator.isPresent()) {
            return existingCollaborator.get().id();
        }
        return insertNewCollaborator(name, cpf);
    }

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

    public Optional<CollaboratorDTO> findCollaboratorByCpf(String cpf) {
        String sql = """
            SELECT * FROM collaborators WHERE cpf = :cpf
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("cpf", cpf);

        List<Object[]> results = query.getResultList();
        return results.stream()
            .findFirst()
            .map(this::mapToCollaboratorDTO);
    }

    private CollaboratorDTO mapToCollaboratorDTO(Object[] result) {
        return new CollaboratorDTO(
            (Long) result[0],
            (String) result[1],
            (String) result[2]
        );
    }
}
