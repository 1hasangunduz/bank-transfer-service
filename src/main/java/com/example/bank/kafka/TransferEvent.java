package com.example.bank.kafka;

import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferEvent {
    private Long from;
    private Long to;
    private BigDecimal amount;
}
