package com.example.bank.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransferEventConsumer {

    @KafkaListener(topics = "transfer-events", groupId = "bank-transfer-consumers")
    public void listen(TransferEvent event) {
        log.info("📥 Kafka'dan transfer event alındı: {}", event);

        // buraya simülasyon da eklenebilir, örneğin:
        // - başka servise çağrı
        // - log DB'ye yazma
        // - async iş tetikleme
    }
}
