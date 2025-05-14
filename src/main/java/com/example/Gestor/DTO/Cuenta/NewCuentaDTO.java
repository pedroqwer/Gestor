package com.example.Gestor.DTO.Cuenta;

import com.example.Gestor.Model.Entity.Perfil;
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
public class NewCuentaDTO implements Serializable {

    private long usuario;
    private BigDecimal saldo;
    private String moneda;
}
