package com.example.Gestor.Model.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Presupuesto")
public class Presupuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "perfil", nullable = false)
    private Perfil perfil;

    @Column(nullable = false,name = "limite_mensual")
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Double cantidadLimite;

    @Column(nullable = false,name = "descripcion")
    @Size(min = 2, max = 999, message = "La descripción debe tener entre 2 y 999 caracteres")
    private String descripcion;

    @Column(name = "presupuesto_del_mes")
    private String presupuestomes;

    @Column(nullable = false,name = "fecha_del_mes")
    private LocalDateTime fecha;
}
