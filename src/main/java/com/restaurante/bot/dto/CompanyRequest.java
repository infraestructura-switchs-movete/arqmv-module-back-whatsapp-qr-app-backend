package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {

    private Long companyId;
    private String nameCompany;
    private String logoUrl;
    private Long numberWhatsapp;
    private String longitude;
    private String latitude;
    private Double baseValue;
    private Double additionalValue;
}
