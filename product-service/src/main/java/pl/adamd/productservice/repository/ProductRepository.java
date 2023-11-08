package pl.adamd.productservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import pl.adamd.productservice.model.Product;

@Repository
public interface ProductRepository extends MongoRepository <Product, String> {
}
