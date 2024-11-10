package com.example.trade.trade_store.controller;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.trade.trade_store.model.Trade;
import com.example.trade.trade_store.service.*;


@RestController
@RequestMapping("/api/trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * @param trade
     * @return
     */
    @PostMapping
    public String addTrade(@RequestBody Trade trade) {
        try {
            tradeService.validateTrade(trade);
            return "Trade added successfully!";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public TradeService getTradeService() {
        return tradeService;
    }
}