package com.demo.pms.client;

import com.demo.pms.client.dto.ExchangeRatesResponse;
import feign.Headers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@FeignClient(value = "exchangeRatesClient",
        url = "${app.clients.exchange-rates.url}",
        fallback = ExchangeRatesClient.ExchangeRatesFallbackImpl.class
)
@Headers("Accept: application/json")
public interface ExchangeRatesClient {

    @GetMapping("/tecajn-eur/v3")
    List<ExchangeRatesResponse> getExchangeRates(@RequestParam("valuta") String currencyCode);

    @Slf4j
    @Component
    class ExchangeRatesFallbackImpl implements ExchangeRatesClient {
        @Override
        public List<ExchangeRatesResponse> getExchangeRates(String currencyCode) {
            log.warn("Fallback method for getExchangeRates() is being used.");
            return Collections.emptyList();
        }
    }

}
