package br.com.dgdev.sulwork.cafe.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.BreakfastDTO;
import br.com.dgdev.sulwork.cafe.exception.ResourceNotFoundException;
import br.com.dgdev.sulwork.cafe.repository.BreakfastRepository;
import br.com.dgdev.sulwork.cafe.repository.ParticipationRepository;

@Service
public class BreakfastService {

	private final BreakfastRepository breakfastRepository;
	private final ParticipationRepository participationRepository;
	private final ItemsService itemsService;

	public BreakfastService(
			BreakfastRepository breakfastRepository,
			ParticipationRepository participationRepository,
			ItemsService itemsService) {
		this.breakfastRepository = breakfastRepository;
		this.participationRepository = participationRepository;
		this.itemsService = itemsService;
	}

	public List<BreakfastDTO> findAllBreakfasts() {
		itemsService.expirePendingItemsFromPastBreakfasts();

		return breakfastRepository.findAllBreakfastRows().stream()
			.map(row -> {
				Long breakfastId = ((Number) row[0]).longValue();
				var participations = participationRepository.findParticipationsByBreakfastId(breakfastId);
				return breakfastRepository.mapToBreakfastDTO(row, participations);
			})
			.toList();
	}

	public BreakfastDTO findBreakfastById(Long id) {
		itemsService.expirePendingItemsFromPastBreakfasts();

		var participations = participationRepository.findParticipationsByBreakfastId(id);
		return breakfastRepository.findBreakfastById(id, participations)
			.orElseThrow(() -> new ResourceNotFoundException("Café da manhã não encontrado!"));
	}

	public Long insertNewBreakfast(LocalDate breakfastDate, LocalTime breakfastTime, String location) {
		Optional<BreakfastDTO> existingBreakfast = breakfastRepository.findBreakfastByDate(breakfastDate);
		if (existingBreakfast.isPresent()) {
			throw new IllegalArgumentException("Café da manhã já existe para a data informada!");
		}
		return breakfastRepository.insertNewBreakfast(breakfastDate, breakfastTime, location.trim());
	}
}
