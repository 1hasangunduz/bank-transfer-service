package com.example.bank.controller;

import com.example.bank.model.Account;
import com.example.bank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestParam String name,
                                                 @RequestParam BigDecimal balance) {
        return ResponseEntity.ok(accountService.createAccount(name, balance));
    }

    @PostMapping("/{id}/update-balance")
    public ResponseEntity<String> updateBalance(@PathVariable Long id,
                                                @RequestParam BigDecimal delta) {
        accountService.updateBalance(id, delta);
        return ResponseEntity.ok("Balance updated");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccount(@PathVariable Long id) {
        return accountService.getAccount(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

}
