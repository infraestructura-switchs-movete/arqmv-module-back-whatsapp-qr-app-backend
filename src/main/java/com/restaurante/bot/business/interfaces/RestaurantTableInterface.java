package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;

import java.util.List;

public interface RestaurantTableInterface {

    RestaurantTable changeStatusOcuped(Long tableNumber);

    RestaurantTable changeStatusFree(Long tableNumber);

    List<RestaurantTable> ListarMesas();

    RestaurantTable addTable();

    GenericResponse deleteTable(Long tableId);

    RestaurantTable changeStatusRequestingService(Long tableNumber);

    RestaurantTable changeStatusReserved(Long tableNumber);
}
