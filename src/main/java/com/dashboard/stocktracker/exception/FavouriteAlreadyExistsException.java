package com.dashboard.stocktracker.exception;

public class FavouriteAlreadyExistsException extends RuntimeException{
    public FavouriteAlreadyExistsException(String symbol){
        super("Stock already saved as favourite: " + symbol);
    }
}
