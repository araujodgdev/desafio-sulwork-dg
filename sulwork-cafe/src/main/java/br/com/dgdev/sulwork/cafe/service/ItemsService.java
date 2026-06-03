package br.com.dgdev.sulwork.cafe.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.ItemDTO;
import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import br.com.dgdev.sulwork.cafe.exception.ResourceNotFoundException;
import br.com.dgdev.sulwork.cafe.repository.ItemsRepository;
import jakarta.transaction.Transactional;

@Service
public class ItemsService {

    private final ItemsRepository itemsRepository;

    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }
    
    @Transactional
    public Long insertNewItem(String name, Long breakfastId, Long participationId) {
        if (itemsRepository.itemExistsInBreakfast(breakfastId, name)) {
            throw new IllegalArgumentException("Item já cadastrado no café da manhã!");
        }
        if (itemsRepository.itemExistsInParticipation(participationId, name)) {
            throw new IllegalArgumentException("Item já cadastrado na participação!");
        }
        return itemsRepository.insertNewItem(name, breakfastId, participationId);
    }

    public Optional<ItemDTO> findItemByItemNameAndBreakfastId(String itemName, Long breakfastId) {
        return itemsRepository.findItemByItemNameAndBreakfastId(itemName, breakfastId);
    }

    @Transactional
    public void expirePendingItemsFromPastBreakfasts() {
        itemsRepository.updatePendingItemsFromPastBreakfasts(LocalDate.now());
    }

    @Transactional
    public void updateItemStatus(Long itemId, ItemStatus status) {
        if (status == ItemStatus.PENDENTE) {
            throw new IllegalArgumentException("Status deve ser TROUXE ou NAO_TROUXE.");
        }

        ItemDTO item = itemsRepository.findItemById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado!"));

        LocalDate breakfastDate = itemsRepository.findBreakfastDateByItemId(item.id())
            .orElseThrow(() -> new ResourceNotFoundException("Café da manhã do item não encontrado!"));

        if (!breakfastDate.equals(LocalDate.now())) {
            throw new IllegalArgumentException("Status do item só pode ser atualizado no dia do café.");
        }

        itemsRepository.updateItemStatus(item.id(), status);
    }

    @Transactional
    public void updateItem(Long itemId, String name) {
        ItemDTO item = itemsRepository.findItemById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado!"));

        Optional<ItemDTO> existingItem = itemsRepository.findItemByItemNameAndBreakfastId(name, item.breakfastId());
        if (existingItem.isPresent() && !existingItem.get().id().equals(itemId)) {
            throw new IllegalArgumentException("Item já cadastrado!");
        }

        itemsRepository.updateItemName(itemId, name);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        int rows = itemsRepository.deleteItem(itemId);
        if (rows == 0) {
            throw new ResourceNotFoundException("Item não encontrado!");
        }
    }
}
