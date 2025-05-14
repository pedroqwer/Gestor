package com.example.Gestor.DTO.Cuenta;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CuentaDTO implements Serializable {

    private long id;
    private String nombreUsuario; // Ahora almacenamos el nombre del usuario
    private BigDecimal saldo;
    private String moneda;

    @Override
    public String toString() {
        return "CuentaDTO " +
                "identificador= " + id+
                ", nombreUsuario= " + nombreUsuario + '\'' +
                ", saldo= " + saldo +
                ", moneda= " + moneda ;
    }
}
