package br.com.dgdev.sulwork.cafe.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import br.com.dgdev.sulwork.cafe.dto.CreateCollaboratorRequestDTO;
import br.com.dgdev.sulwork.cafe.service.CollaboratorService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    public CollaboratorController(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    @PostMapping("/collaborators")
    @Operation(summary = "Cadastrar um novo colaborador")
    @ApiResponse(responseCode = "201", description = "Colaborador cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para cadastro do colaborador")
    @ApiResponse(responseCode = "500", description = "Erro ao cadastrar colaborador")
    public ResponseEntity<Long> createCollaborator(@Valid @RequestBody CreateCollaboratorRequestDTO request) {
        Long collaboratorId = collaboratorService.insertNewCollaborator(request.name(), request.cpf());
        return ResponseEntity.status(HttpStatus.CREATED).body(collaboratorId);
    }
    
}
