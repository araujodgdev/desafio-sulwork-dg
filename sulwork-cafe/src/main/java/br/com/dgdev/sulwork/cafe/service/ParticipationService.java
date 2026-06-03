package br.com.dgdev.sulwork.cafe.service;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.repository.ParticipationRepository;
import jakarta.transaction.Transactional;

@Service
public class ParticipationService {
	
	private final ParticipationRepository participationRepository;
	
	public ParticipationService(ParticipationRepository participationRepository) {
		this.participationRepository = participationRepository;
	};
	
	@Transactional
	public void register() {
	};

}
