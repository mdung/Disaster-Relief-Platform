package com.relief.controller;

import com.relief.entity.InventoryHub;
import com.relief.entity.InventoryStock;
import com.relief.entity.ItemCatalog;
import com.relief.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management endpoints")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/hubs")
    @Operation(summary = "List all inventory hubs")
    public ResponseEntity<List<InventoryHub>> getHubs() {
        return ResponseEntity.ok(inventoryService.getAllHubs());
    }

    @GetMapping("/items")
    @Operation(summary = "List all items in catalog")
    public ResponseEntity<List<ItemCatalog>> getItems() {
        return ResponseEntity.ok(inventoryService.getAllItems());
    }

    @GetMapping("/stock")
    @Operation(summary = "Get stock by hub or item")
    public ResponseEntity<List<InventoryStock>> getStock(
            @RequestParam(required = false) UUID hub,
            @RequestParam(required = false) UUID item
    ) {
        if (hub != null) {
            return ResponseEntity.ok(inventoryService.getStockByHub(hub));
        } else if (item != null) {
            return ResponseEntity.ok(inventoryService.getStockByItem(item));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/stock")
    @Operation(summary = "Update stock quantities")
    public ResponseEntity<InventoryStock> updateStock(@RequestBody UpdateStockRequest request) {
        return ResponseEntity.ok(inventoryService.updateStock(
                request.getHubId(), 
                request.getItemId(), 
                request.getQtyAvailable(), 
                request.getQtyReserved()
        ));
    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock for a task")
    public ResponseEntity<InventoryStock> reserveStock(@RequestBody ReserveStockRequest request) {
        return ResponseEntity.ok(inventoryService.reserveStock(
                request.getHubId(), 
                request.getItemId(), 
                request.getQuantity()
        ));
    }

    @PostMapping("/release")
    @Operation(summary = "Release reserved stock")
    public ResponseEntity<InventoryStock> releaseStock(@RequestBody ReserveStockRequest request) {
        return ResponseEntity.ok(inventoryService.releaseReservation(
                request.getHubId(), 
                request.getItemId(), 
                request.getQuantity()
        ));
    }

    @Data
    public static class UpdateStockRequest {
        private UUID hubId;
        private UUID itemId;
        private Integer qtyAvailable;
        private Integer qtyReserved;
    }

    @Data
    public static class ReserveStockRequest {
        private UUID hubId;
        private UUID itemId;
        private Integer quantity;
    }
}



