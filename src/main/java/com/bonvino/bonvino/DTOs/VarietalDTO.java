package com.bonvino.bonvino.DTOs;

import com.bonvino.bonvino.Models.TipoUva;
import lombok.Data;

@Data
public class VarietalDTO {
    private String descripcion;
    private float porcentajeComposicon;
    private TipoUvaDTO tipoUva;

    public TipoUvaDTO getTypoUva() {
        return tipoUva;
    }
}
