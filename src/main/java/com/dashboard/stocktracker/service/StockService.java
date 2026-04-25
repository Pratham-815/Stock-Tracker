package com.dashboard.stocktracker.service;

import com.dashboard.stocktracker.client.StockClient;
import com.dashboard.stocktracker.dto.*;
import com.dashboard.stocktracker.entity.FavouriteStock;
import com.dashboard.stocktracker.repository.FavouriteStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    private StockClient stockClient;
    private FavouriteStockRepository favouriteStockRepository;

    public StockService(StockClient stockClient, FavouriteStockRepository favouriteStockRepository){
        this.stockClient = stockClient;
        this.favouriteStockRepository = favouriteStockRepository;
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

    public List<DailyStockResponse> getHistory(String symbol, int days){
        StockHistoryResponse response = stockClient.getStockHistory(symbol);

        return response.timeSeries().entrySet().stream()
                .limit(days)
                .map(entry -> {
                    var date = entry.getKey();
                    var daily = entry.getValue();
                    return new DailyStockResponse(
                            date,
                            Double.parseDouble(daily.open()),
                            Double.parseDouble(daily.close()),
                            Double.parseDouble(daily.high()),
                            Double.parseDouble(daily.low()),
                            Long.parseLong(daily.volume())
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public FavouriteStock addFavourite(String symbol){
        if(favouriteStockRepository.existsBySymbol(symbol)){
            throw new RuntimeException("Favourite stock already exists in the list");
        }

        FavouriteStock favourite = FavouriteStock.builder()
                .symbol(symbol)
                .build();

        return favouriteStockRepository.save(favourite);
    }

}
