package com.example.fraud_scanning_service;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record TransactionFraudScanResponse(boolean tenOrMoreIn1Minute,
                                           boolean trnMoreThan300Kms,
                                           boolean countryBlacklisted,
                                           boolean threeDistinctTrnCountriesIn10Minutes) {

  @JsonIgnore
  public boolean isFraudulent() {
    return this.tenOrMoreIn1Minute || this.trnMoreThan300Kms || this.countryBlacklisted ||
        this.threeDistinctTrnCountriesIn10Minutes;
  }
}

