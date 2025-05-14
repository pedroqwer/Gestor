package com.example.Gestor.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "perfil_id", nullable = false)
    private Perfil perfil;  // Relación con Perfil

    @Column(nullable = false, length = 255)
    private String accion; // Ejemplo: "LOGIN", "LOGOUT", "ACTUALIZACIÓN"

    @Column(nullable = false)
    private LocalDateTime fecha; // Fecha de la acción

    @Column(columnDefinition = "TEXT")
    private String detalles; // Información adicional
}
