package com.example.bank;

import com.example.bank.model.Account;
import com.example.bank.repository.AccountRepository;
import com.example.bank.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TransferConcurrencyTest {

    @Autowired
    private TransferService transferService;

    @Autowired
    private AccountRepository accountRepository;

    private Long sourceAccountId;
    private Long targetAccountId;

    @BeforeEach
    public void setup() {
        accountRepository.deleteAll();

        Account from = Account.builder()
                .ownerName("Ali")
                .balance(BigDecimal.valueOf(300))
                .build();

        Account to = Account.builder()
                .ownerName("Veli")
                .balance(BigDecimal.ZERO)
                .build();

        sourceAccountId = accountRepository.save(from).getId();
        targetAccountId = accountRepository.save(to).getId();
    }

    @Test
    public void testConcurrentTransfers_shouldCauseOptimisticLocking() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        Callable<Void> task1 = () -> {
            transferService.transfer(sourceAccountId, targetAccountId, BigDecimal.valueOf(200));
            return null;
        };

        Callable<Void> task2 = () -> {
            Thread.sleep(100); // kısa gecikme ile çakışma tetikleniyor
            transferService.transfer(sourceAccountId, targetAccountId, BigDecimal.valueOf(200));
            return null;
        };

        List<Callable<Void>> tasks = List.of(task1, task2);
        List<Future<Void>> futures = executor.invokeAll(tasks);

        int successCount = 0;
        int failureCount = 0;

        for (Future<Void> future : futures) {
            try {
                future.get();
                successCount++;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof OptimisticLockingFailureException
                        || cause.getMessage().contains("Optimistic")
                        || cause.getMessage().contains("Insufficient funds")) {
                    failureCount++;
                } else {
                    throw new RuntimeException("Beklenmeyen hata: ", e);
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Beklenen sonuçlar
        assertThat(successCount).isEqualTo(1);
        assertThat(failureCount).isEqualTo(1);

        BigDecimal fromBalance = accountRepository.findById(sourceAccountId).get().getBalance();
        BigDecimal toBalance = accountRepository.findById(targetAccountId).get().getBalance();

// Sayısal eşitlik (ondalık farkları göz ardı ederek)
        assertThat(fromBalance).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(toBalance).isEqualByComparingTo(BigDecimal.valueOf(200));
    }
}
