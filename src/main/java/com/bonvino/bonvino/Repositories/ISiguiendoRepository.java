package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.Siguiendo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ISiguiendoRepository extends JpaRepository<Siguiendo,Long> {
}
