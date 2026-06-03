package br.com.dgdev.sulwork.cafe.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.ItemDTO;
import br.com.dgdev.sulwork.cafe.dto.ParticipationDTO;
import br.com.dgdev.sulwork.cafe.exception.ResourceNotFoundException;
import br.com.dgdev.sulwork.cafe.repository.ParticipationRepository;
import jakarta.transaction.Transactional;

@Service
public class ParticipationService {
	
	private final ParticipationRepository participationRepository;
	private final ItemsService itemsService;
	private final CollaboratorService collaboratorService;
	
	public ParticipationService(ParticipationRepository participationRepository, ItemsService itemsService, CollaboratorService collaboratorService) {
		this.participationRepository = participationRepository;
		this.itemsService = itemsService;
		this.collaboratorService = collaboratorService;
	};
	
	@Transactional
	public Long insertNewParticipation(Long breakfastId, String name, String cpf) {

		Long collaboratorId = collaboratorService.findOrCreateCollaborator(name, cpf);

		Optional<ParticipationDTO> existingParticipation = participationRepository.findParticipationByBreakfastIdAndCollaboratorId(breakfastId, collaboratorId);
		if (existingParticipation.isPresent()) {
			throw new IllegalArgumentException("Participação já existe!");
		}
		Long participationId = participationRepository.insertNewParticipation(breakfastId, collaboratorId);


		
		return participationId;
	}

	@Transactional
	public Long insertNewItem(String itemName, Long participationId) {
		ParticipationDTO participation = participationRepository.findParticipationByParticipationId(participationId)
			.orElseThrow(() -> new ResourceNotFoundException("Participação não encontrada!"));

		Optional<ItemDTO> existingItem = itemsService.findItemByItemNameAndBreakfastId(itemName, participation.breakfastId());
		if (existingItem.isPresent()) {
			throw new IllegalArgumentException("Item já cadastrado!");
		}
		return itemsService.insertNewItem(itemName, participation.breakfastId(), participationId);
	}

	public Optional<ParticipationDTO> findParticipationByBreakfastIdAndCollaboratorId(Long breakfastId, Long collaboratorId) {
		return participationRepository.findParticipationByBreakfastIdAndCollaboratorId(breakfastId, collaboratorId);
	}
}
