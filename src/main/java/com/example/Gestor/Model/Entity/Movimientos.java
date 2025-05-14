package com.example.Gestor.Model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Movimientos")
public class Movimientos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "cantidad", nullable = false)
    @Min(value = 1, message = "La cantidad mínima es 1")
    private Double cantidad;

    @Column(name = "descripcion", nullable = false)
    @Size(min = 2, max = 999, message = "La descripción debe tener entre 2 y 999 caracteres")
    private String descripcion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @Column(name = "tipo",nullable = false)
    private Tipo tipo;

    @JsonIgnoreProperties("transacciones")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "is_perfil",referencedColumnName = "id")
    private Perfil perfil;

}
