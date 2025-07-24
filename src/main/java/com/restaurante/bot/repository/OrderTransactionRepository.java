package com.restaurante.bot.repository;

import com.restaurante.bot.dto.OrderResponseDTO;
import com.restaurante.bot.model.OrderTransaction;
import com.restaurante.bot.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderTransactionRepository extends JpaRepository<OrderTransaction, Long> {

    @Query(value = "SELECT " +
                   "SUM(co.total) AS total_order_amount " +
                   "FROM " +
                   "order_transaction ot " +
                   "JOIN " +
                   "customer_order co ON ot.order_id = co.order_id " +
                   "WHERE " +
                   "ot.transaction_id = :transactionId " +
                   "GROUP BY " +
                   "ot.transaction_id " , nativeQuery = true)
    Double getTotalOrderAmount(Long transactionId);

    @Query(value = "SELECT o.orderId AS orderId, op.productId AS productId, op.name AS productName, " +
            "op.quantity AS qty, op.unitePrice AS unitePrice, (op.quantity * op.unitePrice) AS totalPrice, " +
            "o.total AS subTotal " +
            "FROM OrderTransaction ot " +
            "JOIN CustomerOrder o ON ot.orderId = o.orderId " +
            "JOIN OrderProduct op ON o.orderId = op.orderId " +
            "JOIN Transaction t ON ot.transactionId = t.transactionId " +
            "WHERE t.tableId = :tableId", nativeQuery = true)
    List<Object[]> findOrderProductsByTable(@Param("tableId") int tableId);


    @Query(value = "SELECT rt.table_number, ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0), " +
            "o.order_id, op.product_id, op.name, op.quantity, op.unite_price, " +
            "(op.quantity * op.unite_price), o.total " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "WHERE o.`status` = 1 " +
            "GROUP BY rt.table_id, ts.description, o.order_id, op.product_id " +
            "ORDER BY rt.table_id, o.order_id", nativeQuery = true)
    List<Object[]> findAllOrdersGroupedByMesaNative();



    @Query(value = "SELECT rt.table_number, ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0), " +
            "o.order_id, op.product_id, op.name, op.quantity, op.unite_price, " +
            "(op.quantity * op.unite_price), o.total " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "WHERE o.`status` = 2 AND rt.table_number = :tableNumber AND t.`status` = 1 " +
            "GROUP BY rt.table_id, ts.description, o.order_id, op.product_id " +
            "ORDER BY rt.table_id, o.order_id", nativeQuery = true)
    List<Object[]> findAllOrdersEnviadas(@Param("tableNumber")Long tableNumber);

    @Query(value = "SELECT rt.table_number, ts.description, " +
            "COALESCE(SUM(t.transaction_total), 0), " +
            "o.order_id, op.product_id, op.name, op.quantity, op.unite_price, " +
            "(op.quantity * op.unite_price), o.total " +
            "FROM restaurant_table rt " +
            "LEFT JOIN transaction t ON rt.table_id = t.table_id " +
            "LEFT JOIN transaction_status ts ON t.status = ts.transaction_status_id " +
            "LEFT JOIN order_transaction ot ON t.transaction_id = ot.transaction_id " +
            "LEFT JOIN customer_order o ON ot.order_id = o.order_id " +
            "LEFT JOIN order_product op ON o.order_id = op.order_id " +
            "LEFT JOIN customer c ON c.customer_id = t.customer_id " +
            "WHERE o.`status` = 3 AND rt.table_number = :tableNumber AND t.`status` = 1 AND c.phone = :phoneNumber " +
            "GROUP BY rt.table_id, ts.description, o.order_id, op.product_id " +
            "ORDER BY rt.table_id, o.order_id ", nativeQuery = true)
    List<Object[]> findAllOrdersNotConfirm(@Param("tableNumber")Long tableNumber,
                                           @Param("phoneNumber")String phoneNumber);

    OrderTransaction findByOrderId(Long orderId);

    @Query(value = "SELECT " +
            "c.phone, " +
            "op.product_id, " +
            "op.name AS product_name, " +
            "op.quantity AS qty, " +
            "op.unite_price AS price, " +
            "co.total " +
            "FROM  customer c " +
            "JOIN `transaction` t ON c.customer_id = t.customer_id " +
            "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "JOIN order_product op ON op.order_id = co.order_id " +
            "WHERE c.phone = :phoneNumber ", nativeQuery = true)
    List<Object[]> getOrderByPhoneNumber(@Param("phoneNumber")String phoneNumber);


    @Query(value = "SELECT " +
            "c.phone, " +
            "op.product_id, " +
            "op.name AS product_name, " +
            "op.quantity AS qty, " +
            "op.unite_price AS price, " +
            "co.total " +
            "FROM  customer c " +
            "JOIN `transaction` t ON c.customer_id = t.customer_id " +
            "JOIN order_transaction ot ON ot.transaction_id = t.transaction_id " +
            "JOIN customer_order co ON co.order_id = ot.order_id " +
            "JOIN order_product op ON op.order_id = co.order_id " +
            "JOIN restaurant_table r ON r.table_id = t.table_id " +
            "WHERE  r.table_number = :tableNumber ", nativeQuery = true)
    List<Object[]> getOrderByTableNumber(@Param("tableNumber")Integer tableNumber);

}
