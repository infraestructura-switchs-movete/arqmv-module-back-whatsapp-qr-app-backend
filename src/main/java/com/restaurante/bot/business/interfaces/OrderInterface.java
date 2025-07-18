package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.CustomerOrderResponseDTO;
import com.restaurante.bot.dto.OrderDetailsDTO;
import com.restaurante.bot.dto.OrderResponseDTO;
import com.restaurante.bot.model.GenericResponse;

import java.util.List;

public interface OrderInterface {

    GenericResponse saveOrder (OrderDetailsDTO orderDetailsDTO);

    List<OrderResponseDTO> getOrders();

    GenericResponse sendOrderStatus(Long orderId);

    List<OrderResponseDTO> getSendOrder(Long tableNumber);

    CustomerOrderResponseDTO getOrederByPhoneNumber(String phoneNumber);

    CustomerOrderResponseDTO getOrederByTableNumber(Integer tableNumber);
}
