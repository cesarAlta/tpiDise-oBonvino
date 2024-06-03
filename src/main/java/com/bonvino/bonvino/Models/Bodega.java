package com.bonvino.bonvino.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
     *
     * @param fechaActual type LocalDate.
     * @return boolean.
     */
    public boolean validarPeriodicidad(LocalDate fechaActual) {
        return fechaActual.isAfter(ultimaActualizacion.plusMonths(periodoActualizacion));

    }

    /**
     * Recibe una lista de vinos, que son propios de la bodega, y el vino que proviene de la api.
     * <p>
     * El tipo de vino en este caso es VINO, pero podria otarse por un DTO, en caso de que cambie la API.
     * Retorna true si el vino recibido  es de la bodega seleccionada
     *
     * @param nombre   nombre de un vino recivido a comparar.
     * @param añada    añada un vino recivido a comparar.
     * @param misVinos lista de los vinos de la bodega actual.
     * @return boolean .
     */
    public Optional<Vino> esTuVino(String nombre, int añada, List<Vino> misVinos) {
        return misVinos.stream()
                .filter(v -> v.sosEsteVino(nombre, añada))
                .findFirst();
    }

    /**
     * Actualizar vinos
     * Recorro la lista de vinos de la bodega seleccionada hasta obtener el vino a actualizar
     * seteo los nuevos valores y ademas la fecha de actualizacion.
     *
     * @param vinoDataMap key:nombreAtributo vino, value: valorAtributo vino
     * @param vinoBodegaSeleccionadaList lista de todos los vinos de la bodega seleccionada
     * @param fechaActual fecha actual.
     */
    public void actualizarDatosVino(
            Map<String, Object> vinoDataMap,
            List<Vino> vinoBodegaSeleccionadaList,
            LocalDateTime fechaActual) {
        for (Vino vinoDeBodega : vinoBodegaSeleccionadaList) {
            if (vinoDeBodega.sosVinoParaActualizar((String) vinoDataMap.get("nombre"), Integer.parseInt(vinoDataMap.get("añada").toString()))) {
                vinoDeBodega.setPrecioARS((BigDecimal) (vinoDataMap.get("precioARS")));
                vinoDeBodega.setImagenEtiqueta((String) vinoDataMap.get("imagenEtiqueta"));
                vinoDeBodega.setNotaDeCataBodega((String) vinoDataMap.get("notaDeCataBodega"));
                vinoDeBodega.setFechaActualizacion(fechaActual);

            }

        }
    }

    public void setFechaUltimaActualizacion(LocalDate localDate) {
        ultimaActualizacion = localDate;
    }
}
