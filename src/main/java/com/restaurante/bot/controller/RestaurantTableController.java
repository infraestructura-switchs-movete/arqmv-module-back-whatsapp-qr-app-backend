package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.RestaurantTableInterface;
import com.restaurante.bot.model.GenericResponse;
import com.restaurante.bot.model.RestaurantTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/restauranttable")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class RestaurantTableController {

    private final RestaurantTableInterface restaurantTableInterface;


    @GetMapping
    public List<RestaurantTable> ListarMesas() {
        return restaurantTableInterface.ListarMesas();
    }

    @PostMapping
    public ResponseEntity<RestaurantTable> addTable () {
        return new ResponseEntity<>(restaurantTableInterface.addTable(), HttpStatus.OK);
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<GenericResponse> deleteTable (@PathVariable("tableId")Long tableId) {
        return new ResponseEntity<>(restaurantTableInterface.deleteTable(tableId), HttpStatus.OK);
    }

    @PostMapping("/change/status-ocuped")
    public ResponseEntity<RestaurantTable> changeStatusOcuped(@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusOcuped(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-free")
    public ResponseEntity<RestaurantTable> changeStatusFree(@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusFree(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-requesting-service")
    public ResponseEntity<RestaurantTable> changeStatusRequestingService(@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusRequestingService(tableNumber), HttpStatus.OK);
    }

    @PostMapping("/change/status-reserved")
    public ResponseEntity<RestaurantTable> changeStatusReserved(@RequestParam Long tableNumber) {
        return new ResponseEntity<>(restaurantTableInterface.changeStatusReserved(tableNumber), HttpStatus.OK);
    }
}
