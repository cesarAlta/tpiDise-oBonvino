package com.bonvino.bonvino.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contraseña;
    private String nombre;
    private Boolean premiun;

    public Usuario(String contraseña, String usuario, boolean b) {
        this.nombre = usuario;
        this.contraseña=contraseña;
        premiun=b;

    }
}
