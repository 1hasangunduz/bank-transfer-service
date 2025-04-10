package com.example.bank.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransferEventProducer {

    private final KafkaTemplate<String, TransferEvent> kafkaTemplate;

    public void publishTransfer(TransferEvent event) {
        kafkaTemplate.send("transfer-events", event);
    }
}
