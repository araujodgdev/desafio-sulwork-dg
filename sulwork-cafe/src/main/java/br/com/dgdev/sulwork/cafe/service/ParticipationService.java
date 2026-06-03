package br.com.dgdev.sulwork.cafe.service;

import java.net.Authenticator.RequestorType;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.ParticipationRegisterRequestDTO;
import br.com.dgdev.sulwork.cafe.enums.ItemStatus;
import br.com.dgdev.sulwork.cafe.repository.ParticipationRepository;
import jakarta.transaction.Transactional;

@Service
public class ParticipationService {
	
	private final ParticipationRepository participationRepository;
	
	public ParticipationService(ParticipationRepository participationRepository) {
		this.participationRepository = participationRepository;
	};
	
	@Transactional
	public void register(ParticipationRegisterRequestDTO request) {
		validateCollaboratorNotRegistered(request.cpf());
		validateItemsNotRepeatedInRequest();
		validateItemsAvailableOnDate();
		
		Long collaboratorId = participationRepository.insertNewCollaborator(request.collaboratorName(), request.cpf());
		
		for (String item: request.items()) {
			participationRepository.insertNewBreakfastItem(item, request.breakfastDate(), collaboratorId, ItemStatus.PENDENTE);
		}
	};
	
	private void validateCollaboratorNotRegistered(String cpf) {
		if (participationRepository.collaboratorAlreadyExists(cpf)) {
			throw new IllegalArgumentException("CPF já cadastrado!");
		}; 
	};
	
	private void validateItemsNotRepeatedInRequest() {};
	
	private void validateItemsAvailableOnDate() {};
}
