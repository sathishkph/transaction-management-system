package com.outseer.tms.repo;

import com.outseer.tms.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity,String> {

    @Query("SELECT t FROM TransactionEntity t " +
            "WHERE t.userId = :userId " +
            "AND (:startDate IS NULL OR t.timestamp >= :startDate) " +
            "AND (:endDate IS NULL OR t.timestamp <= :endDate)")
    List<TransactionEntity> findTransactionsBetween(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}
