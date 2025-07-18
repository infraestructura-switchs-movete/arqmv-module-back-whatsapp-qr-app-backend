package com.restaurante.bot.controller;

import com.restaurante.bot.business.service.OrderDetailsDeliveryService;
import com.restaurante.bot.dto.OrderDeliveryResponseDTO;
import com.restaurante.bot.dto.OrderDetailsDeliveryDTO;
import com.restaurante.bot.dto.OrderStatusDTO;
import com.restaurante.bot.model.OrderDetailDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/order")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@Slf4j
public class OrderDetailsDeliveryController {
    private final OrderDetailsDeliveryService orderService;

    @PostMapping("/saveOrder")
    public ResponseEntity<OrderDetailDelivery> createOrder(@RequestBody OrderDetailsDeliveryDTO orderDetailsDTO) {
        log.info("Creando una nueva orden");
        try {
            OrderDetailDelivery createdOrder = orderService.saveOrder(orderDetailsDTO);
            return ResponseEntity.status(201).body(createdOrder);
        } catch (Exception e) {
            log.error("Error al crear la orden: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }

    }


    @GetMapping("/get-all-orders")
    public ResponseEntity<List<OrderDeliveryResponseDTO>> getOrders() {
        log.info("Obteniendo todas las órdenes activas");
        try {
            List<OrderDeliveryResponseDTO> activeOrders = orderService.getOrderDetails();
            return ResponseEntity.ok(activeOrders);
        } catch (Exception e) {
            log.error("Error al obtener las órdenes activas: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/updateStatus/{orderTransactionDeliveryId}")
    public ResponseEntity<OrderDetailDelivery> updateOrderStatus(@PathVariable Long orderTransactionDeliveryId, @RequestBody OrderStatusDTO updateOrderStatusDTO) {

        log.info("Actualizando el estado de la orden con ID: {}", orderTransactionDeliveryId);
        try {
            OrderDetailDelivery updatedOrder = orderService.updateOrderStatus(orderTransactionDeliveryId, updateOrderStatusDTO);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            log.error("Error al actualizar el estado de la orden: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }






}
