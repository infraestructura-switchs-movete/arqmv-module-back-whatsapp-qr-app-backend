package com.restaurante.bot.controller;

import com.restaurante.bot.model.Customer;
import com.restaurante.bot.business.service.CustomerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> listarClientes() {
        return customerService.listarClientes(); // Llama al servicio para obtener la lista de clientes
    }

    @PostMapping
    public Customer guardarCliente(@RequestBody Customer customer) {
        return customerService.guardarClientes(customer); // Llama al servicio para guardar un cliente
    }

}
