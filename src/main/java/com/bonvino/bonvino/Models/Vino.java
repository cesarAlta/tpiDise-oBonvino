package com.bonvino.bonvino.Models;

import com.bonvino.bonvino.DTOs.VarietalDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vino {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int añada;
    private LocalDateTime fechaActualizacion;
    private String imagenEtiqueta;
    private String nombre;
    private String notaDeCataBodega;
    private BigDecimal precioARS;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "bodega_id")
    private Bodega bodega;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "vino_id")
    private List<Varietal> varietales;

    @ManyToMany(cascade = CascadeType.ALL)

    private List<Maridaje> maridajes;

    public Vino(int añada, String nombre, BigDecimal precioARS, Bodega bodega, List<Maridaje> maridajes, List<Varietal> varietalList) {
        this.añada = añada;
        this.nombre = nombre;
        this.precioARS = precioARS;
        this.bodega = bodega;
        this.varietales = varietalList;
        this.maridajes = maridajes;
    }

    public Vino(int añada,
                String imagenEtiqueta,
                String nombre,
                String notaDeCataBodega,
                BigDecimal precioARS,
                Bodega bodega,
                List<Maridaje> maridaje
    ) {

        this.añada = añada;
        this.imagenEtiqueta = imagenEtiqueta;
        this.nombre = nombre;
        this.notaDeCataBodega = notaDeCataBodega;
        this.precioARS = precioARS;
        this.bodega = bodega;
        this.maridajes = maridaje;


    }

    /**
     * Recibe un Map key:tipouva, value:varietal para saber el tipo de uva a que varietal le pertenece
     * y asi crear el varietal correspondiente.
     * @param tipoUvaVarietalDTOMap
     */

    public void crearVarietal(Map<TipoUva, VarietalDTO> tipoUvaVarietalDTOMap) {
        varietales = new ArrayList<>();

        for (Map.Entry<TipoUva, VarietalDTO> tipoUvaVarietalMap : tipoUvaVarietalDTOMap.entrySet()) {
            Varietal nuevoVarietal = new Varietal(
                    "Desripcion :" +tipoUvaVarietalMap.getValue().getNombreTipoUva(),
                    tipoUvaVarietalMap.getValue().getPorcentaje(),
                    tipoUvaVarietalMap.getKey());
            varietales.add(nuevoVarietal);
        }

    }

    /**
     * Comparo, a priori comparo el nombre y la añada del el vino recibido con los datos
     * actuales del vino en cuestion.
     * @return true  si el nombre y añáda corresponden al vino en cuestion.
     */

    public boolean sosEsteVino(String nombre, int añada) {
       return  getNombre().equals(nombre) && getAñada() == añada;
    }

    public boolean sosVinoParaActualizar(String nombre, int añada) {
        return getNombre().equals(nombre) && getAñada() == añada;
    }




    @Override
    public String toString() {
        return "Vino{" +
                "añada=" + añada +
                ", fechaActualizacion=" + fechaActualizacion +
                ", imagenEtiqueta='" + imagenEtiqueta + '\'' +
                ", nombre='" + nombre + '\'' +
                ", notaDeCataBodega='" + notaDeCataBodega + '\'' +
                ", precioARS=" + precioARS +
                ", bodega=" + bodega.getNombre() +
                ", varietales=" + varietales +
                ", maridajes=" + maridajes +
                '}';
    }
}



