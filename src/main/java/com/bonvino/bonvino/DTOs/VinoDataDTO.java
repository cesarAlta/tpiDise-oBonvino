package com.bonvino.bonvino.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VinoDataDTO {
    private int añada;
    private LocalDateTime fechaActualizacion;
    private String ImagenEtiqueta;
    private String nombre;
    private String notaDeCataBodega;
    private BigDecimal precioARS;
    private boolean actualizable;
    private List<VarietalDTO> varietales;
    private List<MaridajeDTO> maridajes;



}
