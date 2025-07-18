package com.restaurante.bot.repository;

import com.restaurante.bot.model.RestaurantTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantTableRepository extends JpaRepository<RestaurantTable, Long> {

    RestaurantTable findByTableNumber(Long tableNumber);

    Optional<RestaurantTable> findByTableId(Integer tableId);

    @Query(value = "SELECT MAX(table_number) AS highest_table_number " +
            "FROM restaurant_table ", nativeQuery = true)
    Long findHighestTableNumber();

    boolean existsByTableNumber(Long tableNumber);

    @Query(value = "SELECT rt.tableNumber AS mesa, ts.description AS statusMesa, t.transactionTotal AS totalGeneral " +
            "FROM RestaurantTable rt " +
            "JOIN Transaction t ON rt.tableId = t.tableId " +
            "JOIN TransactionStatus ts ON t.status = ts.transactionStatusId "
            , nativeQuery = true)
    List<Object[]> findAllTablesWithTransactionData();
}
