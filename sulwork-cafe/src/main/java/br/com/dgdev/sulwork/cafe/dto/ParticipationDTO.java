package br.com.dgdev.sulwork.cafe.dto;

import java.time.LocalDateTime;

public record ParticipationDTO(
    Long id,
    Long breakfastId,
    Long collaboratorId
) {
    
}
