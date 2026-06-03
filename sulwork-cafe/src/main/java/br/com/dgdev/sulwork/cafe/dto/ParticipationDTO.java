package br.com.dgdev.sulwork.cafe.dto;

import java.util.List;

public record ParticipationDTO(
    Long id,
    Long breakfastId,
    Long collaboratorId,
    CollaboratorDTO collaborator,
    List<ItemDTO> items
) {
    
}
