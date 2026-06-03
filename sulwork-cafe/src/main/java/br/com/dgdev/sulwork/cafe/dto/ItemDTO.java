package br.com.dgdev.sulwork.cafe.dto;

import br.com.dgdev.sulwork.cafe.enums.ItemStatus;

public record ItemDTO(
    Long id,
    Long breakfastId,
    Long participationId,
    String name,
    ItemStatus status
) {
    
}
