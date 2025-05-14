package com.example.Gestor.Model.Service.Cuenta;

import com.example.Gestor.Model.Entity.CuentaBancaria;
import java.math.BigDecimal;

public interface ICuentaService {

    // Obtener una cuenta bancaria por ID de usuario
    CuentaBancaria obtenerCuentaPorUsuario(Long usuarioId);

    // Consultar el saldo de la cuenta de un usuario
    BigDecimal consultarSaldo(Long usuarioId);

    boolean crearCuentaBancaria(Long usuarioId, BigDecimal saldoInicial, String moneda);

    boolean tieneCuenta(long usuarioId);
}
