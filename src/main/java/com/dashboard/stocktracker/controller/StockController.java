package com.dashboard.stocktracker.controller;

import com.dashboard.stocktracker.dto.DailyStockResponse;
import com.dashboard.stocktracker.dto.StockOverviewResponse;
import com.dashboard.stocktracker.dto.StockResponse;
import com.dashboard.stocktracker.entity.FavouriteStock;
import com.dashboard.stocktracker.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService){
        this.stockService = stockService;
    }

    @GetMapping("/{stockSymbol}")
    public StockResponse getStock(@PathVariable String stockSymbol){
        return stockService.getStockForSymbol(stockSymbol.toUpperCase());
    }

    @GetMapping("/{stockSymbol}/overview")
    public StockOverviewResponse getStockOverview(@PathVariable String stockSymbol){
        return stockService.getStockOverviewForSymbol(stockSymbol.toUpperCase());
    }

    @GetMapping("/{stockSymbol}/history")
    public List<DailyStockResponse> getStockHistory(
            @PathVariable String stockSymbol,
            @RequestParam(defaultValue = "30") int days
    ){
        return stockService.getHistory(stockSymbol.toUpperCase(), days);
    }

    @PostMapping("/favourites")
    public ResponseEntity<FavouriteStock> saveFavouriteStock(@RequestBody FavouriteStock stock ){
        FavouriteStock saved = stockService.addFavourite(stock.getSymbol());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/favourites")
    public List<StockResponse> getFavouritesWithPrices(){
        return stockService.getFavouritesWithLivePrices();
    }
}
