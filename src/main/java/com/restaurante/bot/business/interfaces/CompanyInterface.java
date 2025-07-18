package com.restaurante.bot.business.interfaces;


import com.restaurante.bot.dto.CompanyRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface CompanyInterface {

    @Transactional
    CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile);

    List<CompanyRequest> getAllCompany();

    Boolean delete(Long id);




}
