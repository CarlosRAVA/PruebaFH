package com.proyecto.servicios.repositorys.sf;

import com.proyecto.servicios.entity.sf.Usuario;
import com.proyecto.servicios.model.sf.UsuarioSearchRequest;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UsuarioSpecification {

    public static Specification<Usuario> conFiltros(UsuarioSearchRequest filtros) {
        return (root, query, cb) -> {
            if (filtros == null) {
                return cb.conjunction();
            }

            List<Predicate> predicates = new ArrayList<>();

            if (filtros.getId() != null) {
                predicates.add(cb.equal(root.get("id"), filtros.getId()));
            }

            if (filtros.getCorreo() != null && !filtros.getCorreo().trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("correo")), "%" + filtros.getCorreo().trim().toLowerCase() + "%"));
            }

            if (filtros.getActivo() != null) {
                predicates.add(cb.equal(root.get("activo"), filtros.getActivo()));
            }

            if (filtros.getRol() != null && !filtros.getRol().trim().isEmpty()) {
                predicates.add(cb.equal(cb.upper(root.get("rol")), filtros.getRol().trim().toUpperCase()));
            }

            if (filtros.getClienteId() != null) {
                predicates.add(cb.equal(root.get("cliente").get("id"), filtros.getClienteId()));
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
