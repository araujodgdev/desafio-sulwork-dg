package br.com.dgdev.sulwork.cafe.repository;

import java.util.List;
import java.util.Optional;

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
            SELECT * FROM breakfast_items WHERE item_name = :name
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("name", name);

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

    public Optional<ItemDTO> findItemByItemName(String itemName) {
        String sql = """
            SELECT * FROM breakfast_items WHERE item_name = :itemName
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itemName", itemName);

        List<Object[]> results = query.getResultList();
        return results.stream()
            .findFirst()
            .map(this::mapToItemDTO);
    }

    private ItemDTO mapToItemDTO(Object[] result) {
        return new ItemDTO(
            (Long) result[0],
            (Long) result[1],
            (Long) result[2],
            (String) result[3]
        );
    }
}
