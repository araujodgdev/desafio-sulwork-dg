package br.com.dgdev.sulwork.cafe.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.ParticipationDTO;
import br.com.dgdev.sulwork.cafe.repository.ParticipationRepository;
import jakarta.transaction.Transactional;

@Service
public class ParticipationService {
	
	private final ParticipationRepository participationRepository;
	private final ItemsService itemsService;
	
	public ParticipationService(ParticipationRepository participationRepository, ItemsService itemsService) {
		this.participationRepository = participationRepository;
		this.itemsService = itemsService;
	};
	
	@Transactional
	public ParticipationDTO insertNewParticipation(Long breakfastId, Long collaboratorId) {
		Optional<ParticipationDTO> existingParticipation = participationRepository.findParticipationByBreakfastIdAndCollaboratorId(breakfastId, collaboratorId);
		if (existingParticipation.isPresent()) {
			throw new IllegalArgumentException("Participação já existe!");
		}
		return participationRepository.findParticipationByBreakfastIdAndCollaboratorId(breakfastId, collaboratorId)
			.orElseThrow(() -> new IllegalArgumentException("Participação não encontrada!"));
	}

	public void insertNewItem(String itemName, Long breakfastId, Long participationId) {
		itemsService.insertNewItem(itemName, breakfastId, participationId);
	}
}
