package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.Varietal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IVarietal extends JpaRepository<Varietal,Long> {
}
