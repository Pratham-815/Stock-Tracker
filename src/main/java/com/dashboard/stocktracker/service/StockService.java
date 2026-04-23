package com.dashboard.stocktracker.service;

import com.dashboard.stocktracker.client.StockClient;
import com.dashboard.stocktracker.dto.AlphaVantageResponse;
import com.dashboard.stocktracker.dto.StockOverviewResponse;
import com.dashboard.stocktracker.dto.StockResponse;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private StockClient stockClient;

    public StockService(StockClient stockClient){
        this.stockClient = stockClient;
    }

    public StockResponse getStockForSymbol(String stockSymbol){
        AlphaVantageResponse response = stockClient.getStockQuote(stockSymbol);

        if(response==null || response.globalQuote()==null){
            throw new RuntimeException("Failed to fetch stock data for symbol: "+stockSymbol);
        }

        return StockResponse.builder()
                .symbol(response.globalQuote().symbol())
                .price(Double.parseDouble(response.globalQuote().price()))
                .lastUpdated(response.globalQuote().lastTradingDay())
                .build();
    }

    public StockOverviewResponse getStockOverviewForSymbol(String symbol){
        return stockClient.getStockOverview(symbol);
    }
}
