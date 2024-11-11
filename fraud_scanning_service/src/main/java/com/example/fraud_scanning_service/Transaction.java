package com.example.fraud_scanning_service;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "transactions")
@Data
@Builder
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private final UUID id;

  @Column(name = "tran_id")
  private final UUID transactionId;

  @Column(name = "user_id")
  private final UUID userId;

  @Column
  private final BigInteger amount;

  @Column
  private final LocalDateTime timestamp;

  @Column
  private final String country;

  @Column(name = "lat_coord")
  private final BigDecimal latitude;

  @Column(name = "long_coord")
  private final BigDecimal longitude;

  @Column(name = "is_fraudulent")
  private final boolean isFraudulent;
}
