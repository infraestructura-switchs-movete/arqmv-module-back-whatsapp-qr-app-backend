package com.restaurante.bot.controller;

import com.restaurante.bot.business.interfaces.ProductInterface;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductResponseDTO;
import com.restaurante.bot.model.Product;
import com.restaurante.bot.business.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/${app.request.mapping}/product")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class ProductController {

    private final ProductInterface productInterface;

    @GetMapping("/getProductByCompany/{companyId}")
    public ResponseEntity<CategorizedProductsDTO> getProductsSfotRestaurantByCompanyId(@PathVariable Long companyId) {
        return new ResponseEntity<>(productInterface.getProductsSfotRestaurantByCompanyId(companyId), HttpStatus.OK) ;
    }
}
