package com.restaurante.bot.business.service;

import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.business.call.CallServiceHttp;
import com.restaurante.bot.business.interfaces.ProductInterface;
import com.restaurante.bot.dto.CategorizedProductsDTO;
import com.restaurante.bot.dto.ProductResponseDTO;
import com.restaurante.bot.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService implements ProductInterface {

    private final ProductRepository productRepository;
    private final CallServiceHttp callServiceHttp;

    @Override
    public CategorizedProductsDTO getProductsSfotRestaurantByCompanyId(Long companyId) {

        log.info("Se inicia el llamado a la api que trae los productos de Soft Restaurant para la compañia con id -> {}", companyId);

        // Llamada al servicio que obtiene los productos
        List<ProductDTO> products = callServiceHttp.getProduct(companyId);

        // Crear el objeto que contendrá las categorías y sus productos
        CategorizedProductsDTO categorizedProducts = new CategorizedProductsDTO();

        // Mapear los productos a ProductResponseDTO
        List<ProductResponseDTO> productResponseDTOs = products.stream().map(product -> new ProductResponseDTO(
                        product.getId(),
                        product.getIdProducto(),
                        product.getData().getDescripcion(),
                        product.getData().getPrecio(),
                        product.getData().getGrupo() != null ? product.getData().getGrupo().getIdGrupo() : "",
                        product.getData().getGrupo() != null ? product.getData().getGrupo().getDescripcion() : "",
                        product.getData() != null ? product.getData().getImagenMenu() : ""
                ))
                .collect(Collectors.toList());

        // Definir los IdGrupo de las categorías
        Set<String> bebidasGrupo = new HashSet<>(Arrays.asList("06", "30", "13", "20", "32", "34", "35", "15", "19", "17", "18", "28", "29"));
        Set<String> comidasRapidasGrupo = new HashSet<>(Arrays.asList("02", "31", "39", "07", "41", "04", "03", "40", "10", "08", "09", "27", "23"));
        Set<String> asadosGrupo = new HashSet<>(Arrays.asList("01", "25"));
        Set<String> adicionesGrupo = new HashSet<>(Arrays.asList("12", "22"));

        // Iterar sobre los productos y asignarlos a las categorías correspondientes
        for (ProductResponseDTO product : productResponseDTOs) {
            String idGrupo = product.getCategoryId();

            // Determinar la categoría del producto según su IdGrupo
            if (bebidasGrupo.contains(idGrupo)) {
                categorizedProducts.getBebidas().add(product);
            } else if (comidasRapidasGrupo.contains(idGrupo)) {
                categorizedProducts.getComidasRapidas().add(product);
            } else if (asadosGrupo.contains(idGrupo)) {
                categorizedProducts.getAsados().add(product);
            } else if (adicionesGrupo.contains(idGrupo)) {
                categorizedProducts.getAdiciones().add(product);
            }
        }

        // Retornar los productos organizados por categorías
        return categorizedProducts;
    }
}
