package pl.adamd.productservice.service;

import pl.adamd.productservice.dto.ProductRequest;
import pl.adamd.productservice.dto.ProductResponse;

import java.util.List;

public interface ProductService {

    void createProduct(ProductRequest productRequest);

    List<ProductResponse> getAllProducts();
}
