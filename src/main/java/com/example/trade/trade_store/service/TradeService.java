package com.example.trade.trade_store.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.trade.trade_store.config.DataSourceConfig;
import com.example.trade.trade_store.model.Trade;
import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class TradeService {
    /**
     * @param trade
     * @throws Exception
     */
    private Map<String, Trade> trades = new HashMap<>();
    private  DataSourceConfig databaseConfig = new DataSourceConfig();

    @Autowired
    public TradeService(DataSourceConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    public TradeService() {
        
    }

    private Connection connect() throws SQLException {
        // Retrieve connection details from DatabaseConfig
        return DriverManager.getConnection(
                databaseConfig.getDbUrl(),
                databaseConfig.getDbUsername(),
                databaseConfig.getDbPassword()
        );
    }

  

    public void validateTrade(Trade trade) throws Exception {
        String tradeId = trade.getTradeId();
        LocalDate today = LocalDate.now();

     // Rule 1: Reject trades with a lower version
        if (trades.containsKey(tradeId)) {
            Trade existingTrade = trades.get(tradeId);
            if (trade.getVersion() < existingTrade.getVersion()) {
                throw new Exception("Trade version is lower than the existing version.");
            } else if (trade.getVersion() == existingTrade.getVersion()) {
                // Replace if same version
                commitTrade(trade);
                trades.put(tradeId, trade);
                return;
            }
        }

    // Rule 2: Reject trade if maturity date is earlier than today's date
        if (trade.getMaturityDate().isBefore(today)) {
            throw new Exception("Trade has an expired maturity date.");
        }

        // Store the trade 
        commitTrade(trade);
        trades.put(tradeId, trade);

    }
       
    // Rule 3: Update trades with expired maturity dates
        public void updateExpiryStatus() {
            LocalDate today = LocalDate.now();
            for (Trade trade : trades.values()) {
                if (trade.getMaturityDate().isBefore(today)) {
                    trade.setExpired(true);
                }
            }
        }

         
        // For testing or retrieval
    public Trade getTrade(String tradeId) {
        return trades.get(tradeId);
    }
    

    @Override
	public String toString() {
		return "TradeService []";
	}

    /**
     * @param trade
          * @throws SQLException 
          */
         public void commitTrade(Trade trade) throws SQLException
    {
        try (Connection conn = connect()) {
            String sql = "INSERT INTO trades (trade_id, version, counter_party_id, book_id, maturity_date, created_date, expired) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                         "ON CONFLICT (trade_id, version) DO UPDATE SET " +
                         "counter_party_id = excluded.counter_party_id, " +
                         "book_id = excluded.book_id, " +
                         "maturity_date = excluded.maturity_date, " +
                         "created_date = excluded.created_date, " +
                         "expired = excluded.expired";

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, trade.getTradeId());
                pstmt.setInt(2, trade.getVersion());
                pstmt.setString(3, trade.getCounterPartyId());
                pstmt.setString(4, trade.getBookId());
                pstmt.setDate(5, java.sql.Date.valueOf(trade.getMaturityDate()));
                pstmt.setDate(6, java.sql.Date.valueOf(LocalDate.now()));
                pstmt.setBoolean(7, trade.isExpired());

                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            throw e;
        }

    }

    public void processTrade(String tradeData) {
        try {
            Trade trade = parseTrade(tradeData);
            validateTrade(trade);
        } catch (Exception e) {
            System.err.println("Error processing trade: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Parse the trade data from JSON (or other format) to Trade object
    private Trade parseTrade(String tradeData) throws Exception {
        // Use Jackson or any JSON library to parse
        // Assuming `tradeData` is in JSON format
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(tradeData, Trade.class);
    }

}
