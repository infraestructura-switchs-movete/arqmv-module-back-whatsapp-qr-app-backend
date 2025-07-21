package com.restaurante.bot.controller;

import com.restaurante.bot.model.Customer;
import com.restaurante.bot.business.service.CustomerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/customers")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
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
