package com.bonvino.bonvino.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Siguiendo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    @ManyToOne
    private Bodega bodega;

    public Siguiendo(LocalDateTime localDateTime, Bodega bodega) {
this.fechaInicio = localDateTime;
this.bodega = bodega;
    }

    public boolean sosDeBodega(Bodega bodega){
        return this.bodega.equals(bodega);

    }
}
