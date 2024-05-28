package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.Maridaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMaridajeRepository extends JpaRepository<Maridaje,Long> {
}
