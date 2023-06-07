package com.demo.pms.client.dto;

import com.demo.pms.client.CommaSeparatedNumberDeserializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@Data
public class ExchangeRatesResponse {

    @JsonProperty("datum_primjene")
    private String date;

    @JsonProperty("valuta")
    private String currency;

    @JsonProperty("kupovni_tecaj")
    @JsonDeserialize(using = CommaSeparatedNumberDeserializer.class)
    private double buyingRate;

    @JsonProperty("srednji_tecaj")
    @JsonDeserialize(using = CommaSeparatedNumberDeserializer.class)
    private double middleRate;

    @JsonProperty("prodajni_tecaj")
    @JsonDeserialize(using = CommaSeparatedNumberDeserializer.class)
    private double sellingRate;
}
