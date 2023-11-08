package pl.adamd.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.adamd.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
