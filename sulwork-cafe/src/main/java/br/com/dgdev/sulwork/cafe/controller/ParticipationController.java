package br.com.dgdev.sulwork.cafe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.dgdev.sulwork.cafe.dto.CreateParticipationDTO;
import br.com.dgdev.sulwork.cafe.dto.ParticipationDTO;
import br.com.dgdev.sulwork.cafe.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@Controller
public class ParticipationController {
    
    private final ParticipationService participationService;

    public ParticipationController(ParticipationService participationService) {
        this.participationService = participationService;
    }
    
    @PostMapping("/participations")
    @Operation(summary = "Cadastrar uma nova participação")
    @ApiResponse(responseCode = "201", description = "Participação cadastrada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro da participação")
    @ApiResponse(responseCode = "500", description = "Erro ao cadastrar participação")
    public ResponseEntity<Long> createParticipation(@Valid @RequestBody CreateParticipationDTO request) {
        ParticipationDTO participation = participationService.insertNewParticipation(request.breakfastId(), request.collaboratorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(participation.id());
    }
}
