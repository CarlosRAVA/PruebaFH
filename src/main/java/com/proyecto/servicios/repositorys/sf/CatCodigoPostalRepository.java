package com.proyecto.servicios.repositorys.sf;

import com.proyecto.servicios.entity.sf.CatCodigoPostal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CatCodigoPostalRepository extends JpaRepository<CatCodigoPostal, Long> {
    Optional<CatCodigoPostal> findByCodigo(String codigo);
}
