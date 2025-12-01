package com.relief.service;

import com.relief.entity.InventoryHub;
import com.relief.entity.InventoryStock;
import com.relief.entity.ItemCatalog;
import com.relief.entity.StockMovement;
import com.relief.entity.User;
import com.relief.repository.InventoryHubRepository;
import com.relief.repository.InventoryStockRepository;
import com.relief.repository.ItemCatalogRepository;
import com.relief.repository.StockMovementRepository;
import com.relief.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.relief.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryHubRepository hubRepository;
    private final InventoryStockRepository stockRepository;
    private final ItemCatalogRepository itemRepository;
    private final StockMovementRepository movementRepository;
    private final UserRepository userRepository;

    public List<InventoryHub> getAllHubs() {
        return hubRepository.findAll();
    }

    public List<ItemCatalog> getAllItems() {
        return itemRepository.findAll();
    }

    public List<InventoryStock> getStockByHub(UUID hubId) {
        return stockRepository.findByHubId(hubId);
    }

    public List<InventoryStock> getStockByItem(UUID itemId) {
        return stockRepository.findByItemId(itemId);
    }

    public List<InventoryStock> getAllStock() {
        return stockRepository.findAll();
    }

    public InventoryStock updateStock(UUID hubId, UUID itemId, Integer qtyAvailable, Integer qtyReserved, UUID userId, String reason) {
        InventoryStock stock = stockRepository.findByHubIdAndItemId(hubId, itemId);
        Integer oldQtyAvailable = stock != null ? stock.getQtyAvailable() : 0;
        Integer oldQtyReserved = stock != null ? stock.getQtyReserved() : 0;
        
        if (stock == null) {
            // Create new stock entry
            InventoryHub hub = hubRepository.findById(hubId).orElseThrow();
            ItemCatalog item = itemRepository.findById(itemId).orElseThrow();
            stock = InventoryStock.builder()
                    .hub(hub)
                    .item(item)
                    .qtyAvailable(qtyAvailable != null ? qtyAvailable : 0)
                    .qtyReserved(qtyReserved != null ? qtyReserved : 0)
                    .build();
        } else {
            if (qtyAvailable != null) stock.setQtyAvailable(qtyAvailable);
            if (qtyReserved != null) stock.setQtyReserved(qtyReserved);
        }
        InventoryStock saved = stockRepository.save(stock);
        
        // Track movement if quantities changed
        Integer qtyChange = (qtyAvailable != null ? qtyAvailable : saved.getQtyAvailable()) - oldQtyAvailable;
        if (qtyChange != 0) {
            createMovement(hubId, itemId, qtyChange > 0 ? "in" : "out", Math.abs(qtyChange), 
                    userId, reason != null ? reason : "Stock adjustment");
        }
        
        return saved;
    }

    public InventoryStock reserveStock(UUID hubId, UUID itemId, Integer quantity, UUID userId) {
        InventoryStock stock = stockRepository.findByHubIdAndItemId(hubId, itemId);
        if (stock == null || stock.getQtyAvailable() < quantity) {
            throw new BadRequestException("Insufficient stock available");
        }
        stock.setQtyAvailable(stock.getQtyAvailable() - quantity);
        stock.setQtyReserved(stock.getQtyReserved() + quantity);
        InventoryStock saved = stockRepository.save(stock);
        
        // Track movement
        createMovement(hubId, itemId, "reserve", quantity, userId, "Stock reserved for task");
        
        return saved;
    }

    public InventoryStock releaseReservation(UUID hubId, UUID itemId, Integer quantity, UUID userId) {
        InventoryStock stock = stockRepository.findByHubIdAndItemId(hubId, itemId);
        if (stock == null || stock.getQtyReserved() < quantity) {
            throw new BadRequestException("Insufficient reserved stock");
        }
        stock.setQtyReserved(stock.getQtyReserved() - quantity);
        stock.setQtyAvailable(stock.getQtyAvailable() + quantity);
        InventoryStock saved = stockRepository.save(stock);
        
        // Track movement
        createMovement(hubId, itemId, "release", quantity, userId, "Stock reservation released");
        
        return saved;
    }
    
    private void createMovement(UUID hubId, UUID itemId, String movementType, Integer quantity, UUID userId, String reason) {
        InventoryHub hub = hubRepository.findById(hubId).orElse(null);
        ItemCatalog item = itemRepository.findById(itemId).orElse(null);
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;
        
        StockMovement movement = StockMovement.builder()
                .hub(hub)
                .item(item)
                .movementType(movementType)
                .quantity(quantity)
                .reason(reason)
                .user(user)
                .build();
        movementRepository.save(movement);
    }
    
    public List<StockMovement> getStockMovements(UUID hubId, UUID itemId) {
        if (hubId != null && itemId != null) {
            return movementRepository.findByHubIdAndItemIdOrderByCreatedAtDesc(hubId, itemId);
        } else if (hubId != null) {
            return movementRepository.findByHubIdOrderByCreatedAtDesc(hubId);
        } else if (itemId != null) {
            return movementRepository.findByItemIdOrderByCreatedAtDesc(itemId);
        } else {
            return movementRepository.findAllOrderByCreatedAtDesc();
        }
    }
}


