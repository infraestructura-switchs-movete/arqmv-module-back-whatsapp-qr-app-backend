package com.restaurante.bot.business.call;

import com.restaurante.bot.api.definition.ServiceCallProductsApi;
import com.restaurante.bot.api.dto.ProductDTO;
import com.restaurante.bot.config.ApiConfig;
import com.restaurante.bot.exception.GenericException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CallServiceHttp {

    private final ApiConfig apiConfig;


    public List<ProductDTO> getProduct(Long companyId){
        String apiKey = "23568183-c1ce-2fa9-e063-f501000e7fea";
        //se debe de volver el companyId dinamico.
        //companyId = 238L;
        try {
            ServiceCallProductsApi service = apiConfig.getAllProduct();
            Call<List<ProductDTO>> call = service.getAllProduct(apiKey, companyId);
            Response<List<ProductDTO>> response = call.execute();
            if (response.isSuccessful()) {
                log.info("El servcio de Obtener los productos  respondio -> {}", response.body());
                return response.body();
            } else {
                String errorBody = response.errorBody().string();
                log.error("El servicio de obtener los productos  respondio con error -> code -> {}, message -> {}, headers -> {}", response.code(), errorBody, response.headers());
                throw new GenericException("Datos no encontrado" , HttpStatus.NOT_FOUND);
            }
        } catch (IOException e) {
            log.error("Error al momento de llamar al servicio , Error -> {}", e);
            throw new GenericException("Ocurrio un error al momento de consultar al servicio" , HttpStatus.BAD_REQUEST);
        }
    }


}
