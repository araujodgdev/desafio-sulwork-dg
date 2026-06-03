package br.com.dgdev.sulwork.cafe.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.ItemDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class ItemsRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Long insertNewItem(String name, Long breakfastId, Long participationId) {
        String sql = """
            INSERT INTO breakfast_items (breakfast_id, participation_id, item_name)
            VALUES (:breakfastId, :participationId, :name)
            RETURNING id
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("breakfastId", breakfastId);
        query.setParameter("participationId", participationId);
        query.setParameter("name", name);

        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }


    public Optional<ItemDTO> findItemByName(String name) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name
            FROM breakfast_items
            WHERE item_name = :name
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("name", name);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results.stream()
            .findFirst()
            .map(this::mapToItemDTO);
    }

    public boolean itemExistsInBreakfast(Long breakfastId, String itemName) {
        String sql = """
            SELECT COUNT(*) FROM breakfast_items WHERE breakfast_id = :breakfastId AND item_name = :itemName
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("breakfastId", breakfastId);
        query.setParameter("itemName", itemName);

        Number result = (Number) query.getSingleResult();
        return result.intValue() > 0;
    }

    public boolean itemExistsInParticipation(Long participationId, String itemName) {
        String sql = """
            SELECT COUNT(*) FROM breakfast_items WHERE participation_id = :participationId AND item_name = :itemName
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("participationId", participationId);
        query.setParameter("itemName", itemName);

        Number result = (Number) query.getSingleResult();
        return result.intValue() > 0;
    }

    public Optional<ItemDTO> findItemByItemNameAndBreakfastId(String itemName, Long breakfastId) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name
            FROM breakfast_items
            WHERE item_name = :itemName AND breakfast_id = :breakfastId
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itemName", itemName);
        query.setParameter("breakfastId", breakfastId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results.stream()
            .findFirst()
            .map(this::mapToItemDTO);
    }

    private ItemDTO mapToItemDTO(Object[] row) {
        return new ItemDTO(
            ((Number) row[0]).longValue(),
            ((Number) row[1]).longValue(),
            ((Number) row[2]).longValue(),
            (String) row[3]
        );
    }

    public List<ItemDTO> findItemsByParticipationId(Long participationId) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name
            FROM breakfast_items
            WHERE participation_id = :participationId
            ORDER BY id
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("participationId", participationId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results.stream()
            .map(this::mapToItemDTO)
            .collect(Collectors.toUnmodifiableList());
    }

    public List<ItemDTO> findItemsByBreakfastId(Long breakfastId) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name
            FROM breakfast_items
            WHERE breakfast_id = :breakfastId
            ORDER BY participation_id, id
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("breakfastId", breakfastId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results.stream()
            .map(this::mapToItemDTO)
            .collect(Collectors.toUnmodifiableList());
    }
}
