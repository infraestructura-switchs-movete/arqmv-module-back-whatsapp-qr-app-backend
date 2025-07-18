package com.restaurante.bot.repository;

import com.restaurante.bot.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OrderRepository extends JpaRepository<Order, Long> {
}
