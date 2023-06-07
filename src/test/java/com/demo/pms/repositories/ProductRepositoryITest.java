package com.demo.pms.repositories;

import com.demo.pms.model.Product;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryITest {

    @Autowired
    private ProductRepository productRepository;

    @Container
    @ClassRule
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:11-alpine")
            .withDatabaseName("integration-tests-db").withPassword("inmemory").withUsername("inmemory");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @AfterAll
    public static void stopContainer() {
        postgreSQLContainer.stop();
    }


    @Test
    void saveProduct() {
        Product savedProduct = saveOneProduct();

        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getCode()).isEqualTo("P001");
    }

    @Test
    void findAllProducts() {
        saveOneProduct();
        List<Product> products = productRepository.findAll();
        assertThat(products).isNotEmpty();
    }

    @Test
    void findProductById() {
        Product savedProduct = saveOneProduct();
        Product product = productRepository.findById(savedProduct.getId()).get();
        assertThat(product).isNotNull();
        assertThat(product.getId()).isEqualTo(savedProduct.getId());
    }

    private Product saveOneProduct() {
        Product product = new Product();
        product.setCode("P001");
        product.setName("Sample Product");
        product.setPriceEUR(10.0);
        product.setPriceUSD(12.0);
        product.setDescription("Sample description");
        product.setAvailable(true);

        return productRepository.save(product);
    }
}


