package com.restaurante.bot.repository;


import com.restaurante.bot.model.OrderProductDelivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface OrderProductDeliveryRepository extends JpaRepository<OrderProductDelivery, Long> {

    List<OrderProductDelivery> findByOrderTransactionDeliveryId(Long orderTransactionDeliveryId);


}
