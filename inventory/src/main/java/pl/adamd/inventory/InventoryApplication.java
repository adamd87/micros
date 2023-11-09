package pl.adamd.inventory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import pl.adamd.inventory.model.Inventory;
import pl.adamd.inventory.repository.InventoryRepository;

@SpringBootApplication
@EnableEurekaClient
public class InventoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryApplication.class, args);
	}

	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
		return args -> {
			Inventory inventory = new Inventory();
			inventory.setSkuCode("iPhone");
			inventory.setQuantity(200);

			Inventory inventory2 = new Inventory();
			inventory2.setSkuCode("iPhone_13");
			inventory2.setQuantity(0);

			inventoryRepository.save(inventory);
			inventoryRepository.save(inventory2);
		};
	}

}
