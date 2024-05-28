package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.Enofilo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IEnofiloRepository extends JpaRepository<Enofilo,Long> {
}
