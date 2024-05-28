package com.bonvino.bonvino.Repositories;

import com.bonvino.bonvino.Models.TipoUva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ITipoUvaRepository extends JpaRepository<TipoUva,Long> {
}
