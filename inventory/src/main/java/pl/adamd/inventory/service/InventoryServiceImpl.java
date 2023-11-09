package pl.adamd.inventory.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.adamd.inventory.dto.InventoryResponse;
import pl.adamd.inventory.repository.InventoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode) {
        return inventoryRepository.findBySkuCodeIn(skuCode).stream().map(inventory ->
            InventoryResponse.builder()
                    .skuCode(inventory.getSkuCode())
                    .isInStock(inventory.getQuantity() > 0)
                    .build()
        ).toList();
    }
}
