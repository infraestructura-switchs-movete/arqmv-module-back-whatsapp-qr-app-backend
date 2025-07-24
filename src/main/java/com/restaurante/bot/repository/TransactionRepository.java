package com.restaurante.bot.repository;

import com.restaurante.bot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(value = "SELECT t.* " +
            "FROM transaction t " +
            "JOIN restaurant_table r ON t.table_id = r.table_id " +
            "WHERE r.table_number = :tableNumber AND t.status = 1 ", nativeQuery = true)
    Transaction findTransactionByTableId(Long tableNumber);

    Transaction findByTransactionId(Long id);

    @Query(value = "SELECT DISTINCT t.* " +
            "FROM order_transaction ot " +
            "JOIN `transaction` t ON ot.transaction_id = t.transaction_id " +
            "JOIN restaurant_table r ON r.table_id = t.table_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "WHERE r.table_number = :tableNumber AND co.`status` = 1", nativeQuery = true)
    Transaction getTransactionByTableAndStatus(@Param("tableNumber")Integer tableNumber);

    @Query(value = "SELECT DISTINCT t.* FROM `transaction` t " +
            "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
            "JOIN restaurant_table r ON r.table_id = t.table_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "WHERE r.table_number = :tableNumber AND co.status = 2", nativeQuery = true)
    Transaction getTransactionByTableAndStatusSend(@Param("tableNumber") Integer tableNumber);

    @Query(value = "SELECT t.transaction_id " +
            "FROM `transaction` t " +
            "JOIN customer c ON c.customer_id = t.customer_id " +
            "WHERE c.phone = :phoneNumber AND t.`status` = 1", nativeQuery = true)
    Long getTransactionIdByPhoneNumber(@Param("phoneNumber")String phoneNumber);
}
