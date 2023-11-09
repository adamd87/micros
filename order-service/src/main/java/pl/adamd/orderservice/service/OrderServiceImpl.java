package pl.adamd.orderservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import pl.adamd.orderservice.dto.InventoryResponse;
import pl.adamd.orderservice.dto.OrderLineItemsDto;
import pl.adamd.orderservice.dto.OrderRequest;
import pl.adamd.orderservice.event.OrderPlacedEvent;
import pl.adamd.orderservice.model.Order;
import pl.adamd.orderservice.model.OrderLineItems;
import pl.adamd.orderservice.repository.OrderRepository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    @Override
    public String placeOrder(OrderRequest orderRequest) {
        Order order = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .build();

        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        order.setOrderLineItemsList(orderLineItems);

        List<String> skuCodes = order.getOrderLineItemsList()
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        log.info("Calling inventory service");

        Span inventoryServiceLookup = tracer.nextSpan().name("InventoryServiceLookup");

        try (Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookup.start())) {

            // Call Inventory Service, and place order if product is in stock
            InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)
                    .block();

            boolean allProductsInStock = false;
            if (inventoryResponsesArray != null) {
                allProductsInStock = Arrays.stream(inventoryResponsesArray)
                        .allMatch(InventoryResponse::isInStock);
            }

            if (allProductsInStock) {

                orderRepository.save(order);
                kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));

                return "Order Placed Successfully";
            } else {
                throw new IllegalArgumentException("Product is not in stock");
            }

        } finally {
            inventoryServiceLookup.end();
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {

        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }
}
