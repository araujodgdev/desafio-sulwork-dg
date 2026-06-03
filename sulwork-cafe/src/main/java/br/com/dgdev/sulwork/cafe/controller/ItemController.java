package br.com.dgdev.sulwork.cafe.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.dgdev.sulwork.cafe.dto.UpdateItemRequestDTO;
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

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Atualizar um item")
    @ApiResponse(responseCode = "204", description = "Item atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos para atualização do item")
    @ApiResponse(responseCode = "404", description = "Item não encontrado")
    public ResponseEntity<Void> updateItem(
        @PathVariable Long itemId,
        @Valid @RequestBody UpdateItemRequestDTO request
    ) {
        itemsService.updateItem(itemId, request.name());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Excluir um item")
    @ApiResponse(responseCode = "204", description = "Item excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Item não encontrado")
    public ResponseEntity<Void> deleteItem(@PathVariable Long itemId) {
        itemsService.deleteItem(itemId);
        return ResponseEntity.noContent().build();
    }
}
