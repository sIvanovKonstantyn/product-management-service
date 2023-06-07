package com.demo.pms.rest;

import com.demo.pms.model.Product;
import com.demo.pms.rest.dto.ProductRequest;
import com.demo.pms.rest.dto.ProductResponse;
import com.demo.pms.services.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

@WebMvcTest
@ContextConfiguration(classes = ProductResource.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ProductManagementExceptionHandler.class})
class ProductResourceITest {


    private static final String PRODUCT_API = "/api/v1/products";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductResource controller;

    @MockBean
    private ProductService productService;

    @MockBean
    private ModelMapper mapper;

    @Test
    void getAllProducts() throws Exception {
        Mockito.when(productService.getAll()).thenReturn(List.of(
                Product.builder().id(1L).build(),
                Product.builder().id(2L).build(),
                Product.builder().id(3L).build()
        ));
        Mockito.when(mapper.map(Mockito.any(Product.class), Mockito.any())).thenReturn(
                ProductResponse.builder().id(1L).build(),
                ProductResponse.builder().id(2L).build(),
                ProductResponse.builder().id(3L).build()
        );

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PRODUCT_API);
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertEquals(
                unifyString(allProductsResponse()),
                unifyString(response.getContentAsString())
        );

        Mockito.verify(productService).getAll();
    }

    private String unifyString(String s) {
        return s.replaceAll("\\s+", "")
                .replaceAll("\\n", "");
    }

    private String allProductsResponse() {
        return """
                [
                {"id":1,"code":null,"name":null,"priceEUR":0.0,"priceUSD":0.0,"description":null,"available":false},
                {"id":2,"code":null,"name":null,"priceEUR":0.0,"priceUSD":0.0,"description":null,"available":false},
                {"id":3,"code":null,"name":null,"priceEUR":0.0,"priceUSD":0.0,"description":null,"available":false}
                ]
                """;
    }

    @Test
    void getProductById() throws Exception {
        Mockito.when(productService.getOne(1L)).thenReturn(Product.builder().id(1L).build());
        Mockito.when(mapper.map(Mockito.any(Product.class), Mockito.any()))
                .thenReturn(ProductResponse.builder().id(1L).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(PRODUCT_API.concat("/1"));
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertEquals(
                unifyString(oneProductResponse()),
                unifyString(response.getContentAsString())
        );
        Mockito.verify(productService).getOne(1L);
    }

    private String oneProductResponse() {
        return """
                    {"id":1,"code":null,"name":null,"priceEUR":0.0,"priceUSD":0.0,"description":null,"available":false}
                """;
    }

    @Test
    void createProduct() throws Exception {
        Mockito.when(productService.save(Mockito.any())).thenReturn(Product.builder().id(1L).build());
        Mockito.when(mapper.map(Mockito.any(Product.class), Mockito.any()))
                .thenReturn(ProductResponse.builder().id(1L).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post(PRODUCT_API)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(oneProductRequest());
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();

        Assertions.assertEquals(HttpStatus.CREATED.value(), response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertEquals(
                unifyString(oneProductResponse()),
                unifyString(response.getContentAsString())
        );
        Mockito.verify(productService).save(Mockito.any());
    }

    private String oneProductRequest() {
        return """
                    {"id":1,"code":"1234567890",
                    "name":"product",
                    "priceEUR":0.0,
                    "priceUSD":0.0,
                    "description":null,
                    "available":false}
                """;
    }

    @Test
    void updateProduct() throws Exception {
        Mockito.when(productService.update(Mockito.any())).thenReturn(Product.builder().id(1L).build());
        Mockito.when(mapper.map(Mockito.any(ProductRequest.class), Mockito.any()))
                .thenReturn(Product.builder().id(1L).build());
        Mockito.when(mapper.map(Mockito.any(Product.class), Mockito.any()))
                .thenReturn(ProductResponse.builder().id(1L).build());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(PRODUCT_API.concat("/1"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(oneProductRequest());
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();

        Assertions.assertEquals(HttpStatus.OK.value(), response.getStatus());
        Assertions.assertEquals(MediaType.APPLICATION_JSON_VALUE, response.getContentType());
        Assertions.assertEquals(
                unifyString(oneProductResponse()),
                unifyString(response.getContentAsString())
        );
        Mockito.verify(productService).update(Mockito.any());
    }

    @Test
    void deleteProduct() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete(PRODUCT_API.concat("/1"));
        MockHttpServletResponse response = mockMvc.perform(requestBuilder).andReturn().getResponse();

        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
        Mockito.verify(productService).delete(1L);
    }
}
