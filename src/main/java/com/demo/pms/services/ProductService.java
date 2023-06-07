package com.demo.pms.services;

import com.demo.pms.client.ExchangeRatesClient;
import com.demo.pms.client.dto.ExchangeRatesResponse;
import com.demo.pms.exceptions.NoDataFoundException;
import com.demo.pms.model.Product;
import com.demo.pms.repositories.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ExchangeRatesClient exchangeRatesClient;

    @Autowired
    public ProductService(ProductRepository productRepository, ExchangeRatesClient exchangeRatesClient) {
        this.productRepository = productRepository;
        this.exchangeRatesClient = exchangeRatesClient;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Product getOne(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new NoDataFoundException(
                        String.format("Product with id %d not found", id)
                ));
    }

    public Product save(Product product) {
        updateUsdPrice(product);
        return productRepository.save(product);
    }

    private void updateUsdPrice(Product product) {
        if (product.getPriceEUR() == 0) {
            return;//no need to retrieve usd price if we don't have aht to recalculate
        }
        double usdRate = exchangeRatesClient.getExchangeRates("USD").stream()
                .findAny()
                .orElse(new ExchangeRatesResponse())
                .getMiddleRate();

        if (usdRate == 0) {
            log.warn("USD rate is 0. Please check exchange rates.");
        }
        double usdPrice = usdRate * product.getPriceEUR();
        product.setPriceUSD(usdPrice);
    }

    @Transactional
    public Product update(Product product) {
        Product updatedProduct = getOne(product.getId());
        updatedProduct(product, updatedProduct);
        return productRepository.save(updatedProduct);
    }

    private void updatedProduct(Product product, Product updatedProduct) {
        updatedProduct.setName(product.getName());
        updatedProduct.setCode(product.getCode());
        updatedProduct.setDescription(product.getDescription());
        updatedProduct.setAvailable(product.isAvailable());

        if (updatedProduct.getPriceEUR() != product.getPriceEUR()) {
            updatedProduct.setPriceEUR(product.getPriceEUR());
            updatedProduct.setPriceUSD(0);//we recalculate the price below
            updateUsdPrice(updatedProduct);
        }
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
    }
}
