package br.com.dgdev.sulwork.cafe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.dgdev.sulwork.cafe.dto.BreakfastDTO;
import br.com.dgdev.sulwork.cafe.dto.CreateBreakfastRequestDTO;
import br.com.dgdev.sulwork.cafe.dto.UpdateBreakfastRequestDTO;
import br.com.dgdev.sulwork.cafe.service.BreakfastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class BreakfastController {
    
    private final BreakfastService breakfastService;

    public BreakfastController(BreakfastService breakfastService) {
        this.breakfastService = breakfastService;
    }

    @GetMapping("/breakfasts")
    @Operation(summary = "Listar todos os cafés da manhã")
    @ApiResponse(responseCode = "200", description = "Lista de cafés da manhã encontrada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro ao listar cafés da manhã")
    public ResponseEntity<List<BreakfastDTO>> findAllBreakfasts() {
        return ResponseEntity.ok(breakfastService.findAllBreakfasts());
    }

    @GetMapping("/breakfasts/{id}")
    @Operation(summary = "Buscar café da manhã por ID")
    @ApiResponse(responseCode = "200", description = "Café da manhã encontrado com sucesso")
    @ApiResponse(responseCode = "404", description = "Café da manhã não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro ao buscar café da manhã por ID")
    public ResponseEntity<BreakfastDTO> findBreakfastById(@PathVariable Long id) {
        return ResponseEntity.ok(breakfastService.findBreakfastById(id));
    }

    @PostMapping("/breakfasts")
    @Operation(summary = "Criar um novo café da manhã")
    @ApiResponse(responseCode = "201", description = "Café da manhã criado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para criação do café da manhã")
    @ApiResponse(responseCode = "500", description = "Erro ao criar café da manhã")
    public ResponseEntity<Long> createBreakfast(@Valid @RequestBody CreateBreakfastRequestDTO request) {
        Long breakfastId = breakfastService.insertNewBreakfast(request.breakfastDate(), request.breakfastTime(), request.location());
        return ResponseEntity.status(HttpStatus.CREATED).body(breakfastId);
    }

    @PutMapping("/breakfasts/{id}")
    @Operation(summary = "Atualizar um café da manhã")
    @ApiResponse(responseCode = "204", description = "Café da manhã atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização do café da manhã")
    @ApiResponse(responseCode = "404", description = "Café da manhã não encontrado")
    public ResponseEntity<Void> updateBreakfast(@PathVariable Long id, @Valid @RequestBody UpdateBreakfastRequestDTO request) {
        breakfastService.updateBreakfast(id, request.breakfastDate(), request.breakfastTime(), request.location());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/breakfasts/{id}")
    @Operation(summary = "Excluir um café da manhã")
    @ApiResponse(responseCode = "204", description = "Café da manhã excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Café da manhã não encontrado")
    public ResponseEntity<Void> deleteBreakfast(@PathVariable Long id) {
        breakfastService.deleteBreakfast(id);
        return ResponseEntity.noContent().build();
    }
}
