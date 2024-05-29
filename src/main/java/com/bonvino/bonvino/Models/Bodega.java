package com.bonvino.bonvino.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


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
    private int periodoActualizacion;
    private LocalDate ultimaActualizacion;


    public void contarReseñas() {
    }

    public void mostrarTodosVinos() {
    }

    /**
     * Valida si la bodega esta disponible para obtener actualizacion.
     * Recibe una fecha actual, y verifica que haya pasado desde la ultima actualizacion
     * el priodo de actualizacion.
     * @param fechaActual type LocalDate.
     * @return boolean.
     */
    public boolean validarPeriodicidad(LocalDate fechaActual) {
        return fechaActual.isAfter(ultimaActualizacion.plusMonths(periodoActualizacion));

    }

    /**
     * Recibe una lista de vinos, que son propios de la bodega, y el vino que proviene de la api.
     *
     * El tipo de vino en este caso es VINO, pero podria otarse por un DTO, en caso de que cambie la API.
     * Retorna true si el vino recibido  es de la bodega seleccionada
     * @param vinoRecibido a priori de Tipo Vino, podria optarse por un DTO.
     * @param misVinos lista de los vinos de la bodega seleccionada.
     * @return boolean .
     */
    public boolean esTuVino(Vino vinoRecibido, List<Vino> misVinos) {
//         a cada vino de la bodega seleccionada le pregunto si es el vino recibido
        for (Vino v : misVinos) {
            if (v.sosEsteVino(vinoRecibido)) return true;
        }
        return false;
    }

    /**
     * Actualizar vinos
     * Recorro la lista de vinos de la bodega seleccionada hasta obtener el vino a actualizar
     * seteo los nuevos valores y ademas la fecha de actualizacion.
     * @param vinoRecibido
     * @param vinoBodegaSeleccionadaList
     * @param fechaActual
     */

    public void actualizarDatosVino(Vino vinoRecibido, List<Vino> vinoBodegaSeleccionadaList, LocalDateTime fechaActual) {

        for (Vino vino : vinoBodegaSeleccionadaList) {
            if (vino.sosVinoParaActualizar(vinoRecibido)) {
                vino.setPrecioARS(vinoRecibido.getPrecioARS());
                vino.setImagenEtiqueta(vino.getImagenEtiqueta());
                vino.setNotaDeCataBodega(vinoRecibido.getNotaDeCataBodega());
                vino.setFechaActualizacion(fechaActual);

            }
        }
    }
}
