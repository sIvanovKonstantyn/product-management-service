package com.demo.pms.services;

import com.demo.pms.client.ExchangeRatesClient;
import com.demo.pms.exceptions.NoDataFoundException;
import com.demo.pms.model.Product;
import com.demo.pms.repositories.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ExchangeRatesClient exchangeRatesClient;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository, exchangeRatesClient);
    }

    @Test
    void getAllShouldReturnData() {
        Mockito.when(productRepository.findAll())
                .thenReturn(List.of(
                        new Product(),
                        new Product()
                ));

        Assertions.assertThat(productService.getAll()).hasSize(2);
        Mockito.verify(productRepository).findAll();
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void getOneShouldReturnData() {
        Long testId = 1L;
        Mockito.when(productRepository.findById(testId))
                .thenReturn(Optional.of(
                        new Product()
                ));

        Assertions.assertThat(productService.getOne(testId)).isNotNull();
        Mockito.verify(productRepository).findById(testId);
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void getOneShouldThrowExceptionWhenThereIsNoData() {
        Long testId = 2L;
        Mockito.when(productRepository.findById(testId))
                .thenReturn(Optional.empty());

        assertThrows(
                NoDataFoundException.class,
                () -> productService.getOne(testId)
        );
        Mockito.verify(productRepository).findById(testId);
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void saveShouldCallRepositoryMethod() {
        Product testProduct = new Product();
        productService.save(testProduct);
        Mockito.verify(productRepository).save(testProduct);
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void saveShouldCallRepositoryMethodAndExchangeClientWhenEurPriceIsPresent() {
        Product testProduct = new Product();
        testProduct.setPriceEUR(10);
        productService.save(testProduct);
        Mockito.verify(productRepository).save(testProduct);
        Mockito.verify(exchangeRatesClient).getExchangeRates("USD");
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void updateShouldCallFindOneAndSaveRepositoryMethods() {
        Long testId = 1L;
        Product testProduct = new Product();
        testProduct.setId(testId);
        Mockito.when(productRepository.findById(testId))
                .thenReturn(Optional.of(
                        new Product()
                ));

        productService.update(testProduct);
        Mockito.verify(productRepository).findById(testId);
        Mockito.verify(productRepository).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void updateShouldThrowExceptionWhenThereIsNoItemToUpdate() {
        Long testId = 1L;
        Product testProduct = new Product();
        testProduct.setId(testId);
        Mockito.when(productRepository.findById(testId))
                .thenReturn(Optional.empty());

        assertThrows(
                NoDataFoundException.class,
                () -> productService.update(testProduct)
        );
        Mockito.verify(productRepository).findById(testId);
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void updateShouldCallFindOneAndSaveRepositoryMethodsAndExchangeClientWhenEurPriceWasChanged() {
        Long testId = 1L;
        Product existingProduct = new Product();
        existingProduct.setId(testId);
        existingProduct.setPriceEUR(1L);
        Mockito.when(productRepository.findById(testId))
                .thenReturn(Optional.of(existingProduct));

        Product updatedProduct = new Product();
        updatedProduct.setId(testId);
        updatedProduct.setPriceEUR(10L);
        productService.update(updatedProduct);

        Mockito.verify(productRepository).findById(testId);
        Mockito.verify(exchangeRatesClient).getExchangeRates("USD");
        Mockito.verify(productRepository).save(Mockito.any());
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }

    @Test
    void deleteShouldCallRepositoryMethod() {
        Long testId = 1L;
        productService.delete(testId);
        Mockito.verify(productRepository).deleteById(testId);
        Mockito.verifyNoMoreInteractions(productRepository, exchangeRatesClient);
    }
}
