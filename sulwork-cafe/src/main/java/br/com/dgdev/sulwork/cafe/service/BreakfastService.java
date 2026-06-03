package br.com.dgdev.sulwork.cafe.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import br.com.dgdev.sulwork.cafe.dto.BreakfastDTO;
import br.com.dgdev.sulwork.cafe.repository.BreakfastRepository;

@Service
public class BreakfastService {
    
    private final BreakfastRepository breakfastRepository;

    public BreakfastService(BreakfastRepository breakfastRepository) {
        this.breakfastRepository = breakfastRepository;
    }

    public List<BreakfastDTO> findAllBreakfasts() {
        return breakfastRepository.findAllBreakfasts();
    }

    public BreakfastDTO findBreakfastById(Long id) {
        return breakfastRepository.findBreakfastById(id)
            .orElseThrow(() -> new IllegalArgumentException("Café da manhã não encontrado!"));
    }

    public Long insertNewBreakfast(LocalDate breakfastDate, LocalTime breakfastTime, String location) {
        Optional<BreakfastDTO> existingBreakfast = breakfastRepository.findBreakfastByDate(breakfastDate);
        if (existingBreakfast.isPresent()) {
            throw new IllegalArgumentException("Café da manhã já existe para a data informada!");
        }
        return breakfastRepository.insertNewBreakfast(breakfastDate, breakfastTime, location);
    }
}
