package com.example.fraud_scanning_service;

import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FraudScanningController {

  private final FraudScanningService service;

  @GetMapping("/test")
  public String test() {
    return "Fraud scanning app is working .. ";
  }

  @PostMapping("/scan-transaction")
  public TransactionFraudScanResponse scanTransaction(
      @RequestBody TransactionScanRequest transactionScanRequest)
      throws ExecutionException, InterruptedException {
    return service.isTransactionFraudulent(transactionScanRequest);
  }
}

