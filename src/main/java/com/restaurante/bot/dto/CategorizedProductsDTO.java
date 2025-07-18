package com.restaurante.bot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategorizedProductsDTO {

    private List<ProductResponseDTO> bebidas = new ArrayList<>();
    private List<ProductResponseDTO> comidasRapidas = new ArrayList<>();
    private List<ProductResponseDTO> asados = new ArrayList<>();
    private List<ProductResponseDTO> adiciones = new ArrayList<>();
}
