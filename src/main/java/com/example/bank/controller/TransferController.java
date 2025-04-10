package com.example.bank.controller;

import com.example.bank.service.TransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<String> transfer(@RequestParam Long from,
                                           @RequestParam Long to,
                                           @RequestParam BigDecimal amount) {
        transferService.transfer(from, to, amount);
        return ResponseEntity.ok("Transfer successful");
    }
}
