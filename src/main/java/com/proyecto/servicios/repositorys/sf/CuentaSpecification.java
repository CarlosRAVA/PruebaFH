package com.proyecto.servicios.repositorys.sf;

import com.proyecto.servicios.entity.sf.Cuenta;
import com.proyecto.servicios.model.sf.CuentaSearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CuentaSpecification {

    public static Specification<Cuenta> conFiltros(CuentaSearchRequest filtros) {
        return (root, query, cb) -> {
            if (filtros == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filtros.getClienteId() != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), filtros.getClienteId()));
            }

            if (filtros.getEstatus() != null && !filtros.getEstatus().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("estatus")), filtros.getEstatus().trim().toUpperCase()));
            }

            if (filtros.getNumeroCuenta() != null && !filtros.getNumeroCuenta().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("numeroCuenta"), filtros.getNumeroCuenta().trim()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
