package com.bonvino.bonvino.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class VinoInfoDTO {
    private String nombreYañada;
    private String imagenEtiqueta;
    private String notaCataDeBodega;
    private String precio;
    private List<String> varietales;
    private List<String> maridajes;
    private LocalDateTime fechaActualizacion;
    private String nombreBodeda;
    private String varietalPrincipal;
}
