package com.example.data_app;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionFraudScanningRequest(UUID transactionId,
                                              UUID userId,
                                              BigDecimal amount,
                                              LocalDateTime timestamp,
                                              String country,
                                              String latitude,
                                              String longitude) {
}
