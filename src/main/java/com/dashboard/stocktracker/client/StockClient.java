package com.dashboard.stocktracker.client;

import com.dashboard.stocktracker.dto.AlphaVantageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class StockClient {

    private static final Logger logger = LoggerFactory.getLogger(StockClient.class);

    private final WebClient webClient;

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    public AlphaVantageResponse getStockQuote(String symbol){
        try {
            AlphaVantageResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("function", "GLOBAL_QUOTE")
                            .queryParam("symbol", symbol)
                            .queryParam("apikey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(AlphaVantageResponse.class)
                    .block();

            logger.info("API Response for {}: {}", symbol, response);
            return response;
        } catch (Exception e) {
            logger.error("Error fetching stock data for symbol: {}", symbol, e);
            throw e;
        }
    }
}
