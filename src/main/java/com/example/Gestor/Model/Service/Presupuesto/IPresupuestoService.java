package com.example.Gestor.Model.Service.Presupuesto;

import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.Model.Entity.Presupuesto;
import jakarta.persistence.Column;

import java.time.LocalDateTime;
import java.util.List;

public interface IPresupuestoService {
    //User
    Presupuesto findById(long id);
    boolean Create(Double cantidadLimite, String descripcion,String presupuestomes, long id_user);
    Presupuesto Update(long id, PresupuestoDTO presupuesto);
    void delete(long id);
    List<Presupuesto> findPresupuestoByPerfilAndFecha(Long perfilId, LocalDateTime fecha);
    List<Presupuesto> findAllByUser(long id_User);
    double calcularPorcentajeGasto(long usuarioId);
    List<Presupuesto> findByPerfilId(long perfilId);
    List<Presupuesto> presupuestoactualuser(long perfilId);
}
