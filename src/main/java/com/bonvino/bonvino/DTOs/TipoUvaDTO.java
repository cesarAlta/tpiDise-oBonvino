package com.bonvino.bonvino.DTOs;

import lombok.Data;

@Data
public class TipoUvaDTO {
    private String descripcion;
    private String nombre;

    public boolean sosTipoUva(String nombre) {
        return this.nombre.equals(nombre);
    }
}