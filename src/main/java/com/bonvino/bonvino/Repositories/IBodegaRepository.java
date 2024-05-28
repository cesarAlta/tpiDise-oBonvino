package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.Bodega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBodegaRepository extends JpaRepository<Bodega,Long> {
}
