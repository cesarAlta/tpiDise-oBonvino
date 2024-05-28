package com.bonvino.bonvino.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Maridaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;
    private String nombre;

    @ManyToMany(mappedBy = "maridajes")
    @JsonIgnore
    private List<Vino> vinos = new ArrayList<>();


    public Maridaje( String nombre, String descripcion) {
        this.descripcion = descripcion;
        this.nombre = nombre;
    }

    public boolean sosMaridaje(String meridajeNombre) {
        return nombre.equals(meridajeNombre);
    }



    @Override
    public String toString() {
        return "Maridaje{" +
                "descripcion='" + descripcion + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
