package com.bonvino.bonvino.Models;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Varietal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    private Double porcentajeComposicion;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private TipoUva tipoUva;

    public Varietal(String descripcion, Double porcentajeComposicion, TipoUva tipoUva) {
        this.descripcion = descripcion;
        this.porcentajeComposicion = porcentajeComposicion;
        this.tipoUva = tipoUva;
    }

    public void conocerTipoUva(){

    }

    public void esDetipoUva(){}
    public void mostrarPorcentaje(){}

    @Override
    public String toString() {
        return "Varietal{" +
                "descripcion='" + descripcion + '\'' +
                ", porcentajeComposicion=" + porcentajeComposicion +
                ", tipoUva=" + tipoUva +
                '}';
    }
}
