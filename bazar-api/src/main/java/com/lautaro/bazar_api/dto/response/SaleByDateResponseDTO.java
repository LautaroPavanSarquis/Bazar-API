package com.lautaro.bazar_api.dto.response;

import java.time.LocalDate;
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
public class SaleByDateResponseDTO {

    private LocalDate date;
    private Double total;
    private Integer cantidadVentas;
}

