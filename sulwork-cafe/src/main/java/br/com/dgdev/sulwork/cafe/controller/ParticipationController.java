package br.com.dgdev.sulwork.cafe.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.dgdev.sulwork.cafe.dto.CreateItemRequestDTO;
import br.com.dgdev.sulwork.cafe.dto.CreateParticipationDTO;
import br.com.dgdev.sulwork.cafe.service.ParticipationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
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
        Long participationId = participationService.insertNewParticipation(request.breakfastId(), request.name(), request.cpf());
        return ResponseEntity.status(HttpStatus.CREATED).body(participationId);
    }

    @PostMapping("/participations/{participationId}/items")
    @Operation(summary = "Cadastrar um novo item para uma participação")
    @ApiResponse(responseCode = "201", description = "Item cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro do item")
    @ApiResponse(responseCode = "500", description = "Erro ao cadastrar item")
    public ResponseEntity<Long> createItem(@Valid @RequestBody CreateItemRequestDTO request, @PathVariable Long participationId) {
        Long itemId = participationService.insertNewItem(request.name(), participationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemId);
    }

    @DeleteMapping("/participations/{participationId}")
    @Operation(summary = "Excluir uma participação")
    @ApiResponse(responseCode = "204", description = "Participação excluída com sucesso")
    @ApiResponse(responseCode = "404", description = "Participação não encontrada")
    public ResponseEntity<Void> deleteParticipation(@PathVariable Long participationId) {
        participationService.deleteParticipation(participationId);
        return ResponseEntity.noContent().build();
    }
}
