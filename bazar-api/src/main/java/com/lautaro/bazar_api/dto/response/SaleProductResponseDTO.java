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
public class SaleProductResponseDTO {

    private Long productId;
    private String name;
    private String brand;
    private Integer quantity;
    private Double unitPrice;
}

