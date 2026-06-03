package br.com.dgdev.sulwork.cafe.repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import br.com.dgdev.sulwork.cafe.dto.ItemDTO;
import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.time.LocalDate;

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
        query.setParameter("name", normalizeItemName(name));

        Number result = (Number) query.getSingleResult();
        return result.longValue();
    }

    public boolean itemExistsInBreakfast(Long breakfastId, String itemName) {
        String sql = """
            SELECT COUNT(*) FROM breakfast_items
            WHERE breakfast_id = :breakfastId
              AND LOWER(TRIM(item_name)) = LOWER(TRIM(:itemName))
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("breakfastId", breakfastId);
        query.setParameter("itemName", normalizeItemName(itemName));

        Number result = (Number) query.getSingleResult();
        return result.intValue() > 0;
    }

    public boolean itemExistsInParticipation(Long participationId, String itemName) {
        String sql = """
            SELECT COUNT(*) FROM breakfast_items
            WHERE participation_id = :participationId
              AND LOWER(TRIM(item_name)) = LOWER(TRIM(:itemName))
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("participationId", participationId);
        query.setParameter("itemName", normalizeItemName(itemName));

        Number result = (Number) query.getSingleResult();
        return result.intValue() > 0;
    }

    public Optional<ItemDTO> findItemByItemNameAndBreakfastId(String itemName, Long breakfastId) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name, item_status
            FROM breakfast_items
            WHERE breakfast_id = :breakfastId
              AND LOWER(TRIM(item_name)) = LOWER(TRIM(:itemName))
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itemName", normalizeItemName(itemName));
        query.setParameter("breakfastId", breakfastId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results.stream()
            .findFirst()
            .map(this::mapToItemDTO);
    }

    public Optional<ItemDTO> findItemById(Long itemId) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name, item_status
            FROM breakfast_items
            WHERE id = :itemId
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itemId", itemId);

        @SuppressWarnings("unchecked")
        List<Object[]> results = query.getResultList();
        return results.stream()
            .findFirst()
            .map(this::mapToItemDTO);
    }

    public Optional<LocalDate> findBreakfastDateByItemId(Long itemId) {
        String sql = """
            SELECT b.breakfast_date
            FROM breakfast_items bi
            INNER JOIN breakfasts b ON bi.breakfast_id = b.id
            WHERE bi.id = :itemId
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itemId", itemId);

        @SuppressWarnings("unchecked")
        List<LocalDate> results = query.getResultList();
        return results.stream().findFirst();
    }

    public void updateItemStatus(Long itemId, ItemStatus status) {
        String sql = """
            UPDATE breakfast_items
            SET item_status = :status
            WHERE id = :itemId
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("itemId", itemId);
        query.setParameter("status", status.name());
        query.executeUpdate();
    }

    public int updatePendingItemsFromPastBreakfasts(LocalDate today) {
        String sql = """
            UPDATE breakfast_items
            SET item_status = :status
            WHERE item_status = :pendingStatus
              AND breakfast_id IN (
                SELECT id
                FROM breakfasts
                WHERE breakfast_date < :today
              )
        """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("status", ItemStatus.NAO_TROUXE.name());
        query.setParameter("pendingStatus", ItemStatus.PENDENTE.name());
        query.setParameter("today", today);
        return query.executeUpdate();
    }

    private static String normalizeItemName(String itemName) {
        return itemName.trim();
    }

    private ItemDTO mapToItemDTO(Object[] row) {
        return new ItemDTO(
            ((Number) row[0]).longValue(),
            ((Number) row[1]).longValue(),
            ((Number) row[2]).longValue(),
            (String) row[3],
            ItemStatus.valueOf((String) row[4])
        );
    }

    public List<ItemDTO> findItemsByParticipationId(Long participationId) {
        String sql = """
            SELECT id, breakfast_id, participation_id, item_name, item_status
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
            SELECT id, breakfast_id, participation_id, item_name, item_status
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
