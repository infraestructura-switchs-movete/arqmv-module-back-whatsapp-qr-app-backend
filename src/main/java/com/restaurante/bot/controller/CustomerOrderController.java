package com.restaurante.bot.controller;

import com.restaurante.bot.model.CustomerOrder;
import com.restaurante.bot.business.service.CustomerOrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/customerorder")
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    public CustomerOrderController(CustomerOrderService customerOrderService) {
        this.customerOrderService = customerOrderService;
    }

    @GetMapping
    public List<CustomerOrder> listarOrdenesCliente() {
        return customerOrderService.listarOrdenesCliente();
    }

    @PostMapping
    public CustomerOrder guardarOrdenCliente(@RequestBody CustomerOrder customerOrder) {
        return customerOrderService.guardarOrdenCliente(customerOrder);
    }
}
