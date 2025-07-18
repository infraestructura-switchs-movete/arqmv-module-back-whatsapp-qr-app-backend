package com.restaurante.bot.repository;

import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query(value = "SELECT new com.restaurante.bot.dto.CompanyRequest(c.companyId, c.name, c.logo, c.numberWhatsapp, c.longitude, c.latitude, c.baseValue, c.additionalValue) " +
            "FROM Company c " +
            "WHERE c.status = 'ACTIVE'",
            countQuery = "SELECT COUNT(*) " +
                    "FROM Company c " +
                    "WHERE c.status = 'ACTIVE'")
    List<CompanyRequest> getAllCompany();


}
