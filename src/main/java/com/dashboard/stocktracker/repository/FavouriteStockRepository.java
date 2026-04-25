package com.dashboard.stocktracker.repository;

import com.dashboard.stocktracker.entity.FavouriteStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavouriteStockRepository extends JpaRepository<FavouriteStock, Long> {

    boolean existsBySymbol(String symbol);
}
