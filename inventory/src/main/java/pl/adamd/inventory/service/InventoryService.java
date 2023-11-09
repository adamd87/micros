package pl.adamd.inventory.service;

import pl.adamd.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {
    List<InventoryResponse>  isInStock(List<String> skuCode);
}
