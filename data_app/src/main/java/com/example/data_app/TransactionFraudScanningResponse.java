package com.example.data_app;

public record TransactionFraudScanningResponse(boolean tenOrMoreIn1Minute,
                                               boolean trnMoreThan300Kms,
                                               boolean countryBlacklisted,
                                               boolean threeDistinctTrnCountriesIn10Minutes) {

}
