package com.example.fraud_scanning_service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FraudScanningService {

  private static final HashSet<String> BLACKLISTED_COUNTRIES = new HashSet<>(Arrays.asList(
      "france", "germany", "spain"));

  private final FraudScanningRepository repository;

  public TransactionFraudScanResponse isTransactionFraudulent(TransactionScanRequest dto)
      throws ExecutionException, InterruptedException {
    Instant start = Instant.now();

    var has10OrMoreTransactionsInLastMinute =
        repository.has10OrMoreTransactionInLastMinute(dto.userId());

    var hasTransactionMoreThan300KmsInLast30Minutes =
        repository.hasTransactionMoreThan300KmsInLast30Minutes(dto.userId(), dto.latitude(), dto.longitude());

    var isTransactionCountryBlacklisted = FraudScanningService
        .isTransactionCountryBlacklisted(dto.country());

    var threeDistinctTransactionCountriesForLast10Minutes =
        repository.getDistinctTransactionCountriesForLast10Minutes(dto.userId(), dto.country());

    Instant end = Instant.now();

    System.out.println("Time to process 3 repo methods: " + Duration.between(start, end).toMillis());

    var fraudScanResponse = CompletableFuture.allOf(has10OrMoreTransactionsInLastMinute,
            hasTransactionMoreThan300KmsInLast30Minutes,
            isTransactionCountryBlacklisted,
            threeDistinctTransactionCountriesForLast10Minutes)
        .thenApply(v -> new TransactionFraudScanResponse(
            has10OrMoreTransactionsInLastMinute.join(),
            hasTransactionMoreThan300KmsInLast30Minutes.join(),
            isTransactionCountryBlacklisted.join(),
            threeDistinctTransactionCountriesForLast10Minutes.join()))
        .get();

    saveTransaction(dto, fraudScanResponse);

    return fraudScanResponse;
  }

  public void saveTransaction(TransactionScanRequest dto,
      TransactionFraudScanResponse fraudScanResponse) {
    repository.save(Transaction.builder()
            .transactionId(dto.transactionId())
            .userId(dto.userId())
            .amount(dto.amount().multiply(BigDecimal.valueOf(100)).toBigInteger())
            .timestamp(dto.timestamp())
            .country(dto.country())
            .latitude(dto.latitude())
            .longitude(dto.longitude())
            .isFraudulent(fraudScanResponse.isFraudulent())
        .build());
  }

  public static CompletableFuture<Boolean> isTransactionCountryBlacklisted(String country) {
    return CompletableFuture.completedFuture(BLACKLISTED_COUNTRIES.contains(country.toLowerCase()));
  }
}
