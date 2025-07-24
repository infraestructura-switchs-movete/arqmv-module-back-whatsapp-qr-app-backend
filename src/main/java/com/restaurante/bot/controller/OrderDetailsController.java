package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.OrderInterface;
import com.restaurante.bot.dto.CustomerOrderResponseDTO;
import com.restaurante.bot.dto.OrderDetailsDTO;
import com.restaurante.bot.dto.OrderResponseDTO;
import com.restaurante.bot.model.GenericResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/order")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class OrderDetailsController {

    private final OrderInterface orderInterface;

    @PostMapping
    public ResponseEntity<GenericResponse> saveOrder(@RequestBody OrderDetailsDTO order) {
        log.info("Se inicio el procedimiento de guardar una orden con el request -> {}", order);
        return new ResponseEntity<>(orderInterface.saveOrder(order), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        log.info("Se inicia el servicio que obtiene todas las ordenes activas.");
        return new ResponseEntity<>(orderInterface.getOrders(), HttpStatus.OK);
    }

    @PostMapping("/status/send/{orderId}")
    public ResponseEntity<GenericResponse> sendOrderStatus(@PathVariable("orderId") Long orderId) {
        log.info("Inicicia el servicio que cambia el estado enviado a la orden");
        return new ResponseEntity<>(orderInterface.sendOrderStatus(orderId), HttpStatus.OK);
    }

    @GetMapping("/enviada/{tableNumber}")
    public ResponseEntity<List<OrderResponseDTO>> getSendOrder(@PathVariable("tableNumber")Long tableNumber) {
        log.info("Se inicia el servicio para traer las ordenes enviadas por medio de la mesa");
        return new ResponseEntity<>(orderInterface.getSendOrder(tableNumber), HttpStatus.OK);
    }

    @GetMapping("/by-phone/{phoneNumber}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrederByPhoneNumber(@PathVariable("phoneNumber")String phoneNumber) {
        log.info("se inicia el servicio para traer las ordenes por medio del numero de telefono del cliente -> {}", phoneNumber);
        return new ResponseEntity<>(orderInterface.getOrederByPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/by-table/{tableNumber}")
    public ResponseEntity<CustomerOrderResponseDTO> getOrederByTableNumber(@PathVariable("tableNumber")Integer tableNumber) {
        log.info("se inicia el servicio para traer las ordenes por medio de la mesa del restaurante -> {}", tableNumber);
        return new ResponseEntity<>(orderInterface.getOrederByTableNumber(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/confirmation")
    public ResponseEntity<GenericResponse> confirmationOrder(@RequestParam String phoneNumber) {
        log.info("Se inicia el servicio que confirma la orden");
        return new ResponseEntity<>(orderInterface.confirmationOrder(phoneNumber), HttpStatus.OK);
    }

    @GetMapping("/no-confirmed")
    public ResponseEntity<List<OrderResponseDTO>> noConfirmationOrder(@RequestParam String phoneNumber,
                                                                      @RequestParam Long tableNumber) {
        log.info("Se inicia el servicio que trae las ordenes no confirmadas");
        return new ResponseEntity<>(orderInterface.noConfirmationOrder(tableNumber, phoneNumber), HttpStatus.OK);
    }

}
