package com.example.fraud_scanning_service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record TransactionScanRequest(UUID transactionId,
                                     UUID userId,
                                     BigDecimal amount,
                                     LocalDateTime timestamp,
                                     String country,
                                     BigDecimal latitude,
                                     BigDecimal longitude) {
}
