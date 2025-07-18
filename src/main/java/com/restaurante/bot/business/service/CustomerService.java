package com.restaurante.bot.business.service;


import com.restaurante.bot.model.Customer;
import com.restaurante.bot.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> listarClientes() {
        return customerRepository.findAll(); //Esta lista usa el objeto Customer para obtener todos los clientes
    }

    public Customer guardarClientes (Customer customer){
        return customerRepository.save(customer); //Guarda un cliente en la base de datos
    }
}
