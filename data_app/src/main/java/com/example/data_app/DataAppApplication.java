package com.example.data_app;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class DataAppApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(DataAppApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		WebClient webClient = WebClient.builder()
				.baseUrl("http://localhost:8081")
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();

		UUID testUserID = UUID.randomUUID();

		System.out.println("Begin creating transactions for user with ID: " + testUserID);

		sendTenTransactionsInOneMinute(testUserID, webClient);
		sendTransactionMoreThan300Km(testUserID, webClient);
		sendBlacklistedTransaction(testUserID, webClient);
		send3TransactionFromDifferentCountries(testUserID, webClient);

	}

	private static void sendTenTransactionsInOneMinute(UUID testUserID, WebClient webClient) {
		for (int i = 0; i < 10; i++) {
			TransactionFraudScanningRequest request =
					new TransactionFraudScanningRequest(
							UUID.randomUUID(),
							testUserID,
							BigDecimal.ONE,
							LocalDateTime.now(),
							"Japan",
							"35.652832",
							"139.839478");

			ResponseEntity<TransactionFraudScanningResponse> response = webClient.post()
					.uri("/scan-transaction")
					.body(Mono.just(request), TransactionFraudScanningRequest.class)
					.retrieve()
					.toEntity(TransactionFraudScanningResponse.class)
					.block();

			System.out.println(i == 9 ? "Processed 10th transaction. Response should show it's fraudulent: " + response.toString() : response.toString());
		}

	}

	private void sendTransactionMoreThan300Km(UUID testUserID, WebClient webClient) {
		TransactionFraudScanningRequest request =
				new TransactionFraudScanningRequest(
						UUID.randomUUID(),
						testUserID,
						BigDecimal.ONE,
						LocalDateTime.now(),
						"UK",
						"51.509865",
						"-0.118092");

		ResponseEntity<TransactionFraudScanningResponse> response = webClient.post()
				.uri("/scan-transaction")
				.body(Mono.just(request), TransactionFraudScanningRequest.class)
				.retrieve()
				.toEntity(TransactionFraudScanningResponse.class)
				.block();

		System.out.println("Transaction from more than 300 kms:" + response.toString());
	}

	private void sendBlacklistedTransaction(UUID testUserID, WebClient webClient) {
		TransactionFraudScanningRequest request =
				new TransactionFraudScanningRequest(
						UUID.randomUUID(),
						testUserID,
						BigDecimal.ONE,
						LocalDateTime.now(),
						"Spain",
						"40.416775",
						"-3.703790");

		ResponseEntity<TransactionFraudScanningResponse> response = webClient.post()
				.uri("/scan-transaction")
				.body(Mono.just(request), TransactionFraudScanningRequest.class)
				.retrieve()
				.toEntity(TransactionFraudScanningResponse.class)
				.block();

		System.out.println("Transaction from blacklisted country: " + response.toString());
	}

	private void send3TransactionFromDifferentCountries(UUID testUserID, WebClient webClient) {
		TransactionFraudScanningRequest request =
				new TransactionFraudScanningRequest(
						UUID.randomUUID(),
						testUserID,
						BigDecimal.ONE,
						LocalDateTime.now(),
						"Belgium",
						"50.85045",
						"4.34878");

		ResponseEntity<TransactionFraudScanningResponse> response = webClient.post()
				.uri("/scan-transaction")
				.body(Mono.just(request), TransactionFraudScanningRequest.class)
				.retrieve()
				.toEntity(TransactionFraudScanningResponse.class)
				.block();

		System.out.println("This transaction is from a 3rd country in the last 30 minutes: " + response.toString());
	}
}
