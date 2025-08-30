package com.outseer.tms.repo;

import com.outseer.tms.dto.AccountDto;
import com.outseer.tms.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    Page<TransactionEntity> findTransactionsBetween(
            @Param("userId") String userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query("SELECT new com.outseer.tms.dto.AccountDto(t.userId, COALESCE(SUM(t.amount), 0), COUNT(t)) " +
            "FROM TransactionEntity t WHERE t.userId = :userId GROUP BY t.userId")
    AccountDto findBalanceAndCountByUserId(@Param("userId") String userId);

}
