package com.lautaro.bazar_api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleResponseDTO {

    private Long id;
    private Double total;
    private Integer cantidadProductos;
    private String nombreCliente;
    private String apellidoCliente;
}

