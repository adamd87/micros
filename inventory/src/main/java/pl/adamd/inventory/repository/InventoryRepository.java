package pl.adamd.inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.adamd.inventory.model.Inventory;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {


    Optional<Inventory> findBySkuCode(String skuCode);

    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
