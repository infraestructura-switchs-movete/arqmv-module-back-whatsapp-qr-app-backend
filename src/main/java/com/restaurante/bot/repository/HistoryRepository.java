package com.restaurante.bot.repository;

import com.restaurante.bot.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
