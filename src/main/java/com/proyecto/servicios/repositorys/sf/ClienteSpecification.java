package com.proyecto.servicios.repositorys.sf;

import com.proyecto.servicios.entity.sf.Cliente;
import com.proyecto.servicios.model.sf.ClienteSearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ClienteSpecification {

    public static Specification<Cliente> conFiltros(ClienteSearchRequest filtros) {
        return (root, query, cb) -> {
            if (filtros == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filtros.getId() != null) {
                predicates.add(cb.equal(root.get("id"), filtros.getId()));
            }

            if (filtros.getNombre() != null && !filtros.getNombre().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filtros.getNombre().trim().toLowerCase() + "%"));
            }

            if (filtros.getApellidoPaterno() != null && !filtros.getApellidoPaterno().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("apellidoPaterno")), "%" + filtros.getApellidoPaterno().trim().toLowerCase() + "%"));
            }

            if (filtros.getApellidoMaterno() != null && !filtros.getApellidoMaterno().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("apellidoMaterno")), "%" + filtros.getApellidoMaterno().trim().toLowerCase() + "%"));
            }

            if (filtros.getCurp() != null && !filtros.getCurp().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("curp")), filtros.getCurp().trim().toUpperCase()));
            }

            if (filtros.getRfc() != null && !filtros.getRfc().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("rfc")), filtros.getRfc().trim().toUpperCase()));
            }

            if (filtros.getCorreoElectronico() != null && !filtros.getCorreoElectronico().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("correoElectronico")), "%" + filtros.getCorreoElectronico().trim().toLowerCase() + "%"));
            }

            if (filtros.getActivo() != null) {
                predicates.add(cb.equal(root.get("activo"), filtros.getActivo()));
            }

            if (filtros.getFechaInicio() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"), filtros.getFechaInicio()));
            }

            if (filtros.getFechaFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaCreacion"), filtros.getFechaFin()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
