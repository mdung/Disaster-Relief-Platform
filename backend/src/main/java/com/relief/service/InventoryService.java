package com.relief.service;

import com.relief.entity.InventoryHub;
import com.relief.entity.InventoryStock;
import com.relief.entity.ItemCatalog;
import com.relief.repository.InventoryHubRepository;
import com.relief.repository.InventoryStockRepository;
import com.relief.repository.ItemCatalogRepository;
import lombok.RequiredArgsConstructor;
import com.relief.exception.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private final InventoryHubRepository hubRepository;
    private final InventoryStockRepository stockRepository;
    private final ItemCatalogRepository itemRepository;

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

    public InventoryStock updateStock(UUID hubId, UUID itemId, Integer qtyAvailable, Integer qtyReserved) {
        InventoryStock stock = stockRepository.findByHubIdAndItemId(hubId, itemId);
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
        return stockRepository.save(stock);
    }

    public InventoryStock reserveStock(UUID hubId, UUID itemId, Integer quantity) {
        InventoryStock stock = stockRepository.findByHubIdAndItemId(hubId, itemId);
        if (stock == null || stock.getQtyAvailable() < quantity) {
            throw new BadRequestException("Insufficient stock available");
        }
        stock.setQtyAvailable(stock.getQtyAvailable() - quantity);
        stock.setQtyReserved(stock.getQtyReserved() + quantity);
        return stockRepository.save(stock);
    }

    public InventoryStock releaseReservation(UUID hubId, UUID itemId, Integer quantity) {
        InventoryStock stock = stockRepository.findByHubIdAndItemId(hubId, itemId);
        if (stock == null || stock.getQtyReserved() < quantity) {
            throw new BadRequestException("Insufficient reserved stock");
        }
        stock.setQtyReserved(stock.getQtyReserved() - quantity);
        stock.setQtyAvailable(stock.getQtyAvailable() + quantity);
        return stockRepository.save(stock);
    }
}


