package br.com.dgdev.sulwork.cafe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.dgdev.sulwork.cafe.dto.UpdateItemStatusRequestDTO;
import br.com.dgdev.sulwork.cafe.service.ItemsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
public class ItemController {

    private final ItemsService itemsService;

    public ItemController(ItemsService itemsService) {
        this.itemsService = itemsService;
    }

    @PatchMapping("/items/{itemId}/status")
    @Operation(summary = "Atualizar status de um item")
    @ApiResponse(responseCode = "204", description = "Status atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Status inválido ou data bloqueada")
    @ApiResponse(responseCode = "404", description = "Item não encontrado")
    public ResponseEntity<Void> updateStatus(
        @PathVariable Long itemId,
        @Valid @RequestBody UpdateItemStatusRequestDTO request
    ) {
        itemsService.updateItemStatus(itemId, request.status());
        return ResponseEntity.noContent().build();
    }
}
