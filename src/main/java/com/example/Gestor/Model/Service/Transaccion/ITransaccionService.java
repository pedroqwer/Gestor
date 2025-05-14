package com.example.Gestor.Model.Service.Transaccion;

import com.example.Gestor.DTO.Transaccion.TransaccionDTO;
import com.example.Gestor.Model.Entity.Movimientos;
import com.example.Gestor.Model.Entity.Tipo;

import java.math.BigDecimal;
import java.util.List;

public interface ITransaccionService {
    //User
    Movimientos findById(long id);
    boolean Create(long perfil, double cantidad, String descripcion, Tipo tipo);
    Movimientos Update(long id, TransaccionDTO transaccion );
    //void delete(long id);

    List<Movimientos> findAllByUsuario(long idUsuario);
    List<Movimientos> filterByCategoria(long idUsuario, Tipo tipo);
    BigDecimal obtenerTotalIngresos(long id);
    BigDecimal obtenerTotalGastos(long id);
}
