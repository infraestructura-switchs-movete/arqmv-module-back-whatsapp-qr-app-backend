package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.OrderDeliveryResponseDTO;
import com.restaurante.bot.dto.OrderDetailsDTO;
import com.restaurante.bot.dto.OrderDetailsDeliveryDTO;
import com.restaurante.bot.model.OrderDetailDelivery;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IOrderDetailBusiness {


    @Transactional
    OrderDetailDelivery saveOrder(OrderDetailsDeliveryDTO orderDetailsDTO);

    List<OrderDeliveryResponseDTO> getOrderDetails();

    Boolean delete(Long id);
}
