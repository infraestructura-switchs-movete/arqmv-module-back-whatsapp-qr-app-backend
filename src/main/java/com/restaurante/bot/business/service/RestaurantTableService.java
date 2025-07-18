package com.restaurante.bot.business.service;

import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.exception.GenericException;
import com.restaurante.bot.model.Customer;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import com.restaurante.bot.repository.RestaurantTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RestaurantTableService implements RestaurantTableInterface {

    private final RestaurantTableRepository restaurantTableRepository;

    @Override
    public List<RestaurantTable> ListarMesas(){
        return restaurantTableRepository.findAll();
    }

    @Override
    public RestaurantTable addTable() {

        Long highestTableNumber = restaurantTableRepository.findHighestTableNumber();
        if (highestTableNumber == null) {
            highestTableNumber = 0L; // Asignar 0 si es null
        }

        Long newNumberTable = highestTableNumber + 1;

        RestaurantTable newTable = new RestaurantTable();
        newTable.setTableNumber(newNumberTable);
        newTable.setStatus(1L);
        return restaurantTableRepository.save(newTable);

    }

    @Override
    public GenericResponse deleteTable(Long tableId) {

        if (!restaurantTableRepository.existsById(tableId)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        restaurantTableRepository.deleteById(tableId);
        return new GenericResponse("Mesa eliminada con exito", 200L);
    }

    @Override
    public RestaurantTable changeStatusOcuped(Long tableNumber) {

        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(2L);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusFree(Long tableNumber) {
        if (!restaurantTableRepository.existsByTableNumber(tableNumber)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(1L);

        return restaurantTableRepository.save(table);
    }

    @Override
    public RestaurantTable changeStatusRequestingService(Long tableNumber) {
        if (!restaurantTableRepository.existsByTableNumber(tableNumber)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(3L);

        return restaurantTableRepository.save(table);
    }

    public RestaurantTable changeStatusReserved(Long tableNumber) {
        if (!restaurantTableRepository.existsByTableNumber(tableNumber)) {
            throw new GenericException("Mesa no resgistrada en la base de datos", HttpStatus.BAD_REQUEST);
        }
        RestaurantTable table = restaurantTableRepository.findByTableNumber(tableNumber);
        table.setStatus(4L);

        return restaurantTableRepository.save(table);
    }
}
