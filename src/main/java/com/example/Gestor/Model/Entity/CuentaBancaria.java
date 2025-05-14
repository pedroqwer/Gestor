package com.example.Gestor.Model.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cuenta_bancaria")
public class CuentaBancaria {

    @Id
    private long id; // ID ahora será un número de 4 dígitos

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id", nullable = false, unique = true)
    private Perfil usuario;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;

    @Column(nullable = false)
    private String moneda;

    @PrePersist
    public void generarId() {
        this.id = (int) (10000 + Math.random() * 90000); // Genera un ID entre 10000 y 99999
    }

}
