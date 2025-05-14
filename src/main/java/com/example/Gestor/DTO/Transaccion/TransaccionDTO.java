package com.example.Gestor.DTO.Transaccion;

import com.example.Gestor.Model.Entity.Tipo;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransaccionDTO implements Serializable {

    private long id;
    private Double cantidad;
    private String descripcion;
    //private String presupuestomes;
    private LocalDateTime fecha;
    private Tipo tipo;

}
