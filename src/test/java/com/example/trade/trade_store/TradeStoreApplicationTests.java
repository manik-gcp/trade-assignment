package com.example.trade.trade_store;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.trade.trade_store.model.Trade;
import com.example.trade.trade_store.service.TradeService;

@SpringBootTest
class TradeStoreApplicationTests {

	private TradeService tradeservice;

	

	 @BeforeEach
    public void setUp() {
        tradeservice = new TradeService();
    }

 @Test
    public void testAddTradeWithLowerVersion() throws Throwable {
        Trade tradeV2 = new Trade("T1", 2, "CP-1", "B1", LocalDate.now().plusDays(5), LocalDate.now());
        tradeservice.validateTrade(tradeV2);

        Trade tradeV1 = new Trade("T1", 1, "CP-1", "B1", LocalDate.now().plusDays(5), LocalDate.now());

        Exception exception = assertThrows(Exception.class, () -> tradeservice.validateTrade(tradeV1));
        assertEquals("Trade version is lower than the existing version.", exception.getMessage());
    }

    

	 @Test
    public void testAddTradeWithExpiredMaturityDate() {
        Trade expiredTrade = new Trade("T2", 1, "CP-2", "B1", LocalDate.now().minusDays(1), LocalDate.now());

        Exception exception = assertThrows(Exception.class, () -> tradeservice.validateTrade(expiredTrade));
        assertEquals("Trade has an expired maturity date.", exception.getMessage());
    }

    /**
		 * @throws Exception 
		 * 
		 */
		@Test
		public void testUpdateExpiryStatus() throws Exception {
        Trade trade = new Trade("T3", 1, "CP-3", "B2", LocalDate.now().minusDays(1), LocalDate.now());
       
			tradeservice.validateTrade(trade);
		

        tradeservice.updateExpiryStatus();
        assertTrue(tradeservice.getTrade("T3").isExpired());
    }



}
