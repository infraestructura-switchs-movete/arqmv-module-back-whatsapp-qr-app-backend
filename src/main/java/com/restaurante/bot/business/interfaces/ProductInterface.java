package com.restaurante.bot.business.interfaces;

import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductResponseDTO;

import java.util.List;

public interface ProductInterface {

    CategorizedProductsDTO getProductsSfotRestaurantByCompanyId(Long companyId);
}
