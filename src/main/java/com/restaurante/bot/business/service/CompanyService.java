package com.restaurante.bot.business.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.restaurante.bot.business.interfaces.CompanyInterface;
import com.restaurante.bot.dto.CompanyRequest;
import com.restaurante.bot.model.Company;
import com.restaurante.bot.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Slf4j
public class CompanyService implements CompanyInterface {

    private final CompanyRepository companyRepository;
    private final Cloudinary cloudinary;

    @Override
    public CompanyRequest save(CompanyRequest companyRequest, MultipartFile logoFile) {
        try {
            Map uploadResult = cloudinary.uploader().upload(logoFile.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            String logoUrl = (String) uploadResult.get("url");

            Company company = new Company();
            company.setName(companyRequest.getNameCompany());
            company.setNumberWhatsapp(companyRequest.getNumberWhatsapp());
            company.setLongitude(companyRequest.getLongitude());
            company.setLatitude(companyRequest.getLatitude());
            company.setBaseValue(companyRequest.getBaseValue());
            company.setAdditionalValue(companyRequest.getAdditionalValue());
            company.setLogo(logoUrl);
            company.setStatus("ACTIVE");

            Company savedCompany = companyRepository.save(company);

            companyRequest.setCompanyId(savedCompany.getCompanyId());
            companyRequest.setLogoUrl(savedCompany.getLogo());

            return companyRequest;

        } catch (IOException e) {
            log.error("Error al subir la imagen del logo", e);
            throw new RuntimeException("Error al subir la imagen del logo", e);
        }
    }

    @Override
    public List<CompanyRequest> getAllCompany() {
        return companyRepository.getAllCompany();
    }

    @Override
    public Boolean delete(Long id) {
        if (companyRepository.existsById(id)) {
            Company company = companyRepository.findById(id).get();
            company.setStatus("INACTIVE");
            companyRepository.save(company);
            return true;
        } else {
            throw new RuntimeException("La compañia no fue encontrada por el id " + id);
        }
    }

}
