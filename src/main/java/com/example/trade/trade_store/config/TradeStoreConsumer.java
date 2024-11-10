package com.example.trade.trade_store.config;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.example.trade.trade_store.service.TradeService;

public class TradeStoreConsumer {

    private static final String TOPIC = "trade-topic";

    /**
     * @param args
     */
    public static void main(String[] args) {
        KafkaConsumer<String, String> consumer = KafkaConfig.createConsumer(TOPIC);
        TradeService tradeStoreService = new TradeService();

        try {
            while (true) {
                @SuppressWarnings("deprecation")
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    String tradeData = record.value();
                    tradeStoreService.processTrade(tradeData);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in Kafka consumer: " + e.getMessage());
            e.printStackTrace();
        } finally {
            consumer.close();
        }
    }
}
