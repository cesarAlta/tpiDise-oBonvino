package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.Bodega;
import com.bonvino.bonvino.Models.Vino;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IVinoRepository extends JpaRepository<Vino,Long> {
    List<Vino> findByBodega(Bodega bodegaSeleccionada);

}
