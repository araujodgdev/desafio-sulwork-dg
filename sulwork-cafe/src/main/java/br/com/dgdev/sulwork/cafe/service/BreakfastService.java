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
import jakarta.transaction.Transactional;

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

	@Transactional
	public Long insertNewBreakfast(LocalDate breakfastDate, LocalTime breakfastTime, String location) {
		Optional<BreakfastDTO> existingBreakfast = breakfastRepository.findBreakfastByDate(breakfastDate);
		if (existingBreakfast.isPresent()) {
			throw new IllegalArgumentException("Café da manhã já existe para a data informada!");
		}
		return breakfastRepository.insertNewBreakfast(breakfastDate, breakfastTime, location.trim());
	}

	@Transactional
	public void updateBreakfast(Long id, LocalDate breakfastDate, LocalTime breakfastTime, String location) {
		if (breakfastRepository.findBreakfastRowById(id).isEmpty()) {
			throw new ResourceNotFoundException("Café da manhã não encontrado!");
		}

		Optional<BreakfastDTO> existingBreakfast = breakfastRepository.findBreakfastByDateIgnoringId(breakfastDate, id);
		if (existingBreakfast.isPresent()) {
			throw new IllegalArgumentException("Café da manhã já existe para a data informada!");
		}

		breakfastRepository.updateBreakfast(id, breakfastDate, breakfastTime, location.trim());
	}

	@Transactional
	public void deleteBreakfast(Long id) {
		int rows = breakfastRepository.deleteBreakfast(id);
		if (rows == 0) {
			throw new ResourceNotFoundException("Café da manhã não encontrado!");
		}
	}
}
