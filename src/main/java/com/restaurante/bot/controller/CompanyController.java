package com.restaurante.bot.controller;


import com.restaurante.bot.business.interfaces.CompanyInterface;
import com.restaurante.bot.dto.CompanyRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@CrossOrigin(origins = "http://localhost:5174")
@Slf4j
public class CompanyController {

    private final CompanyInterface companyService;

    @PostMapping("/create")
    public ResponseEntity<CompanyRequest> createCompany(
            @RequestParam("nameCompany") String nameCompany,
            @RequestParam("numberWhatsapp") Long numberWhatsapp,
            @RequestParam("longitude") String longitude,
            @RequestParam("latitude") String latitude,
            @RequestParam("baseValue") Double baseValue,
            @RequestParam("additionalValue") Double additionalValue,
            @RequestParam("logo") MultipartFile logo) {

        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setNameCompany(nameCompany);
        companyRequest.setNumberWhatsapp(numberWhatsapp);
        companyRequest.setLongitude(longitude);
        companyRequest.setLatitude(latitude);
        companyRequest.setBaseValue(baseValue);
        companyRequest.setAdditionalValue(additionalValue);

        CompanyRequest savedCompany = companyService.save(companyRequest, logo);

        return ResponseEntity.ok(savedCompany);
    }


    @GetMapping("/get-company")
    public ResponseEntity<List<CompanyRequest>> getAllCategories() {
        log.info("Iniciando endpoint para obtener todas las compañias");
        List<CompanyRequest> categories = companyService.getAllCompany();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
