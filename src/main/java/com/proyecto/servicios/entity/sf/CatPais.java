package com.proyecto.servicios.entity.sf;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_paises")
@Data
public class CatPais {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;
}
