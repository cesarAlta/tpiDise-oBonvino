package com.bonvino.bonvino.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Enofilo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String apellido;
    private String imagenPerfil;
    private String nombre;
    @OneToMany(cascade = CascadeType.PERSIST)
    private List<Siguiendo> siguiendoList;
    @OneToOne(cascade = CascadeType.PERSIST)
    private Usuario usuario;



    public boolean seguisABodega(Bodega bodegaSeleccionada) {
        return siguiendoList.stream().anyMatch(siguiendo -> siguiendo.sosDeBodega(bodegaSeleccionada));
    }


    public String getNombreUsuario() {
        return usuario.getNombre();
    }
}
