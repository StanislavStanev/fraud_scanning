package com.example.fraud_scanning_service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;

@Repository
public interface FraudScanningRepository extends JpaRepository<Transaction, UUID> {

  @Async
  @Query(value = """
      SELECT COUNT(*) >= 9
        FROM transactions t
       WHERE user_id = ?1
         AND t."timestamp" > NOW() - INTERVAL '1 Minutes';
      """, nativeQuery = true)
  CompletableFuture<Boolean> has10OrMoreTransactionInLastMinute(UUID userId);

  @Async
  @Query(value = """
      WITH
      older_trns AS (
          SELECT t.lat_coord as lat,
                 t.long_coord as long
           FROM transactions t
          WHERE t.user_id = ?1
          AND t."timestamp" > NOW() - INTERVAL '30 Minutes'
      )
      SELECT
      COALESCE(BOOL_OR((earth_distance(
              ll_to_earth(older_trns.lat, older_trns.long),
              ll_to_earth(?2, ?3))/1000)::integer > 300), FALSE)
      FROM older_trns;
      """, nativeQuery = true)
  CompletableFuture<Boolean> hasTransactionMoreThan300KmsInLast30Minutes(UUID userId, BigDecimal latitude, BigDecimal longitude);

  @Async
  @Query(value = """
      SELECT COUNT(*) >= 3
      FROM (
            SELECT DISTINCT t.country
              FROM transactions t
             WHERE t.user_id = ?1
               AND t."timestamp" > now() - interval '10 Minutes'
             UNION
            SELECT ?2) as all_countries;
      """, nativeQuery = true)
  CompletableFuture<Boolean> getDistinctTransactionCountriesForLast10Minutes(UUID userId, String country);

}
