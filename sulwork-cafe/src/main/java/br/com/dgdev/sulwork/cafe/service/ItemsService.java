package br.com.dgdev.sulwork.cafe.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.ItemDTO;
import br.com.dgdev.sulwork.cafe.repository.ItemsRepository;

@Service
public class ItemsService {

    private final ItemsRepository itemsRepository;

    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }
    
    public Long insertNewItem(String name, Long breakfastId, Long participationId) {
        if (itemsRepository.findItemByName(name).isPresent()) {
            throw new IllegalArgumentException("Item já cadastrado!");
        }
        if (itemsRepository.itemExistsInBreakfast(breakfastId, name)) {
            throw new IllegalArgumentException("Item já cadastrado no café da manhã!");
        }
        if (itemsRepository.itemExistsInParticipation(participationId, name)) {
            throw new IllegalArgumentException("Item já cadastrado na participação!");
        }
        return itemsRepository.insertNewItem(name, breakfastId, participationId);
    }

    public Optional<ItemDTO> findItemByItemName(String itemName) {
        return itemsRepository.findItemByItemName(itemName);
    }
}
