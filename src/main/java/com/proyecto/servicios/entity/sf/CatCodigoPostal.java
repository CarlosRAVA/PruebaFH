package com.proyecto.servicios.entity.sf;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_codigos_postales")
@Data
public class CatCodigoPostal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 10)
    private String codigo;
}
