package com.example.Gestor.DTO.Presupuesto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewPresupuestoDTO implements Serializable {

    private Double cantidadLimite;
    private String descripcion;
    private String presupuesto_del_mes;
    private long id_user;
}
