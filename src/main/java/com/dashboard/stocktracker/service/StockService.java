package com.dashboard.stocktracker.service;

import com.dashboard.stocktracker.client.StockClient;
import com.dashboard.stocktracker.dto.*;
import com.dashboard.stocktracker.entity.FavouriteStock;
import com.dashboard.stocktracker.exception.FavouriteAlreadyExistsException;
import com.dashboard.stocktracker.repository.FavouriteStockRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockClient stockClient;
    private final FavouriteStockRepository favouriteStockRepository;

    public StockService(StockClient stockClient, FavouriteStockRepository favouriteStockRepository){
        this.stockClient = stockClient;
        this.favouriteStockRepository = favouriteStockRepository;
    }

    @Cacheable(value = "stocks", key = "#stockSymbol")
    public StockResponse getStockForSymbol(String stockSymbol){
        try {
            AlphaVantageResponse response = stockClient.getStockQuote(stockSymbol);

            // Debug log
            System.out.println("Fetching stock: " + stockSymbol);
            System.out.println("API response: " + response);

            // Handle API failure / rate limit / invalid response
            if(response == null || response.globalQuote() == null){
                System.out.println("Invalid API response for: " + stockSymbol);
                return null;
            }

            String priceStr = response.globalQuote().price();

            // Handle empty or invalid price
            if(priceStr == null || priceStr.isEmpty()){
                System.out.println("Invalid price for: " + stockSymbol);
                return null;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                System.out.println("Price parsing failed for: " + stockSymbol);
                return null;
            }

            return StockResponse.builder()
                    .symbol(response.globalQuote().symbol())
                    .price(price)
                    .lastUpdated(response.globalQuote().lastTradingDay())
                    .build();

        } catch (Exception e) {
            // Catch EVERYTHING so one failure doesn't kill the API
            System.out.println("Error fetching stock: " + stockSymbol);
            e.printStackTrace();
            return null;
        }
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
            throw new FavouriteAlreadyExistsException(symbol);
        }

        FavouriteStock favourite = FavouriteStock.builder()
                .symbol(symbol)
                .build();

        return favouriteStockRepository.save(favourite);
    }

    public List<StockResponse> getFavouritesWithLivePrices(){
        List<FavouriteStock> favourites = favouriteStockRepository.findAll();

        return favourites.stream()
                .map(fav -> getStockForSymbol(fav.getSymbol()))
                .filter(Objects::nonNull) // prevents crash
                .collect(Collectors.toList());
    }
}