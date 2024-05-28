package com.bonvino.bonvino.Models;

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
    @JoinTable(
            name = "vino_maridaje",
            joinColumns = @JoinColumn(name = "vino_id"),
            inverseJoinColumns = @JoinColumn(name = "maridaje_id")
    )
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
                LocalDateTime fechaActualizacion,
                String imagenEtiqueta,
                String nombre,
                String notaDeCataBodega,
                BigDecimal precioARS,
                Bodega bodega,
                List<Maridaje> maridaje, Map<TipoUva, Varietal> tipoUvaVarietalDTOMap
    ) {

        this.añada = añada;
        this.fechaActualizacion = fechaActualizacion;
        this.imagenEtiqueta = imagenEtiqueta;
        this.nombre = nombre;
        this.notaDeCataBodega = notaDeCataBodega;
        this.precioARS = precioARS;
        this.bodega = bodega;
        this.maridajes = maridaje;

        crearVarietal(tipoUvaVarietalDTOMap);


    }

    public Vino(int añada, String nombre, BigDecimal precioARS, Bodega bodega, List<Maridaje> maridajes, Map<Double, TipoUva> varietal) {
        this.añada = añada;
        this.nombre = nombre;
        this.precioARS = precioARS;
        this.bodega = bodega;
        this.maridajes = maridajes;
        crearVarietals(varietal);
    }

    private void crearVarietals(Map<Double, TipoUva> tipoUvaMap) {
        varietales = new ArrayList<>();
        for (Map.Entry<Double, TipoUva> tipoUvaVarietalMap : tipoUvaMap.entrySet()) {
            Varietal nuevoVarietal = new Varietal(
                    "Descripcio de " + tipoUvaVarietalMap.getValue().getNombre(),
                    tipoUvaVarietalMap.getKey(),
                    tipoUvaVarietalMap.getValue());
            varietales.add(nuevoVarietal);
        }
    }

    private void crearVarietal(Map<TipoUva, Varietal> tipoUvaVarietalDTOMap) {
        varietales = new ArrayList<>();
        for (Map.Entry<TipoUva, Varietal> tipoUvaVarietalMap : tipoUvaVarietalDTOMap.entrySet()) {
            Varietal nuevoVarietal = new Varietal(
                    tipoUvaVarietalMap.getValue().getDescripcion(),
                    tipoUvaVarietalMap.getValue().getPorcentajeComposicion(),
                    tipoUvaVarietalMap.getKey());
            varietales.add(nuevoVarietal);
        }

    }

    public boolean sosEsteVino(Vino vinoRecibido) {
       return  vinoRecibido.getNombre().equals(nombre) && vinoRecibido.getAñada() == this.añada;
    }

    public boolean sosVinoParaActualizar(Vino vinoRecibido) {
        return  vinoRecibido.getNombre().equals(nombre) && vinoRecibido.getAñada() == this.añada;
//        return this.equals(vinoRecibido);
    }

    public boolean sosDeEstaBodega(Bodega bodegaSeleccionada) {
        return this.bodega.equals(bodegaSeleccionada);
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



