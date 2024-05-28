package com.bonvino.bonvino.Models;

import com.bonvino.bonvino.DTOs.VinoDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bodega {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String[] coordenadasUbicacion;
    private String descripcion;
    private String historia;
    private String nombre;
    private int periodoActializacion;
    private LocalDate ultimaActualizacion;


    public void contarReseñas() {
    }

    public void mostrarTodosVinos() {
    }

    public boolean validarPeriodicidad(LocalDate fechaActual) {
        return fechaActual.isAfter(ultimaActualizacion.plusMonths(periodoActializacion));


    }

    public boolean esTuVino(Vino vinoRecibido, List<Vino> misVinos) {
        for (Vino v : misVinos) {
            if (v.sosEsteVino(vinoRecibido)) return true;
        }
        return false;
    }

    public void actualizarDatosVino(Vino vinoRecibido, List<Vino> vinoBodegaSeleccionadaList, LocalDateTime fechaActual) {

        for (Vino vino : vinoBodegaSeleccionadaList) {
            if (vino.sosVinoParaActualizar(vinoRecibido)) {
                vino.setPrecioARS(vinoRecibido.getPrecioARS());
                vino.setImagenEtiqueta(vino.getImagenEtiqueta());
                vino.setNotaDeCataBodega(vinoRecibido.getNotaDeCataBodega());
                vino.setFechaActualizacion(fechaActual);
                vino.setImagenEtiqueta(vino.getImagenEtiqueta());

            }
        }
    }
}
