package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.TransactionInterface;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.model.Transaction;
import com.restaurante.bot.repository.*;
import jakarta.persistence.Table;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TransactionService implements TransactionInterface {

    private final RestaurantTableRepository restaurantTableRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final OrderProductRepository orderProductRepository;
    private final OrderTransactionRepository orderTransactionRepository;
    private final HistoryRepository historyRepository;


    @Override
    public GenericResponse finalizeTransaction(Integer tableNumber) {

        Transaction transaction = transactionRepository.getTransactionByTableAndStatus(tableNumber);
        if (transaction != null) {
            throw new GenericException("hay ordenes por enviar en esat mesa", HttpStatus.BAD_REQUEST);
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber.longValue());
        table.setStatus(1L);
        restaurantTableRepository.save(table);

        Transaction transaction2 = transactionRepository.getTransactionByTableAndStatusSend(tableNumber);
        transaction2.setStatus(2L);
        transactionRepository.save(transaction2);
        return new GenericResponse("Transaccion finalizada con exito", 200L);

    }
}
