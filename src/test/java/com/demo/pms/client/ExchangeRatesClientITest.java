package com.demo.pms.client;

import com.demo.pms.client.dto.ExchangeRatesResponse;
import com.demo.pms.wiremock.WireMockInitializer;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.client.MockRestServiceServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(classes = {ExchangeRatesClient.class})
@EnableFeignClients
@ImportAutoConfiguration({FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
@ContextConfiguration(initializers = WireMockInitializer.class)
@ExtendWith(MockitoExtension.class)
class ExchangeRatesClientITest {

    private static final String BASE_URL = "ttp://localhost:57253";
    private static final String EXCHANGE_URL = BASE_URL + "/tecajn-eur/v3?valuta=%s";
    private static final String USD_CURRENCY_CODE = "USD";
    private static final double EXPECTED_RATE = 1.6219;
    @Autowired
    private ExchangeRatesClient exchangeRatesClient;

    private MockRestServiceServer server;

    @Autowired
    private WireMockServer wireMockServer;

    @BeforeEach
    void setUp() {
//        RestTemplate restTemplate = new RestTemplate();
//        server = MockRestServiceServer.createServer(restTemplate);
//        server.expect(requestTo(String.format(EXCHANGE_URL, USD_CURRENCY_CODE)))
//                .andExpect(method(HttpMethod.GET))
//                .andRespond(withSuccess()
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .body(exchangeResponse())
//                );

        wireMockServer.stubFor(get(urlEqualTo("/tecajn-eur/v3?valuta=USD"))
                .withQueryParam("valuta", equalTo(USD_CURRENCY_CODE))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(exchangeResponse())));
    }

    private String exchangeResponse() {
        return """
                [
                    {
                        "broj_tecajnice": "109",
                        "datum_primjene": "2023-06-06",
                        "drzava": "Australija",
                        "drzava_iso": "AUS",
                        "sifra_valute": "036",
                        "valuta": "AUD",
                        "kupovni_tecaj": "1,6243",
                        "srednji_tecaj": "1,6219",
                        "prodajni_tecaj": "1,6195"
                    }
                ]""";
    }


    @Test
    void getExchangeRates() {
        var exchangeRates = exchangeRatesClient.getExchangeRates(USD_CURRENCY_CODE);
        Assertions.assertThat(exchangeRates).isNotEmpty();
        Assertions.assertThat(exchangeRates).hasSize(1);
        Assertions.assertThat(exchangeRates.get(0)).isInstanceOf(ExchangeRatesResponse.class);
        Assertions.assertThat(exchangeRates.get(0).getMiddleRate()).isEqualTo(EXPECTED_RATE);
    }
}
