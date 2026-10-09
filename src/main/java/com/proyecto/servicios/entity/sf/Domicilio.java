package com.proyecto.servicios.entity.sf;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "domicilios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Domicilio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 100)
    private String calle;

    @Column(name = "numero_exterior", nullable = false, length = 20)
    private String numeroExterior;

    @Column(name = "numero_interior", length = 20)
    private String numeroInterior;

    @Column(nullable = false, length = 100)
    private String colonia;

    @Column(nullable = false, length = 100)
    private String municipio;

    @ManyToOne
    @JoinColumn(name = "estado_id", nullable = false)
    private CatEstado estado;

    @ManyToOne
    @JoinColumn(name = "codigo_postal_id", nullable = false)
    private CatCodigoPostal codigoPostal;
}
