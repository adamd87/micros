package pl.adamd.orderservice.service;

import pl.adamd.orderservice.dto.OrderRequest;

public interface OrderService {
    String placeOrder(OrderRequest orderRequest);
}
