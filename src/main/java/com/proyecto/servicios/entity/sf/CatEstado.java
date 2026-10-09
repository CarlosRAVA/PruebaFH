package com.proyecto.servicios.entity.sf;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cat_estados")
@Data
public class CatEstado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "pais_id", nullable = false)
    private CatPais pais;
}
