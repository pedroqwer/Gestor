package com.example.Gestor.Model.Service.Presupuesto;

import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.Exception.CreateEntityException;
import com.example.Gestor.Exception.DeleteEntityException;
import com.example.Gestor.Exception.NotFoundEntityException;
import com.example.Gestor.Exception.UpdateEntityException;
import com.example.Gestor.Model.Entity.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import com.example.Gestor.Model.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PresupuestoService implements IPresupuestoService {

    @Autowired
    private IRepositoryPresupuesto repositoryPresupuesto;

    @Autowired
    private IRepositoryPerfil repositoryPerfil;

    @Autowired
    private IRepositoryCuentaBancaria repositoryCuentaBancaria;

    @Autowired
    private IRepositoryTransaccion repositoryTransaccion;

    @Autowired
    private IAuditoriaRepository auditoriaRepository;

    @Override
    public List<Presupuesto> findAllByUser(long id_User) {
        return repositoryPresupuesto.findAllByUser(id_User);
    }

    @Override
    public double calcularPorcentajeGasto(long usuarioId) {
        try {
            // 1️⃣ Obtener el mes actual en español
            String mesActual = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es")).toLowerCase();

            // 2️⃣ Buscar el presupuesto más reciente del usuario
            List<Presupuesto> presupuestos = repositoryPresupuesto.findAllByUser(usuarioId);

            if (presupuestos.isEmpty()) {
                throw new RuntimeException("No tiene presupuesto");  // Aquí lanzamos el mensaje cuando no haya presupuesto
            }

            // Obtener el presupuesto más reciente
            Presupuesto presupuesto = presupuestos.get(0);

            // 3️⃣ Validar si el presupuesto es del mes actual
            if (presupuesto.getFecha() == null ||
                    !presupuesto.getFecha().getMonth().equals(LocalDate.now().getMonth())) {
                throw new RuntimeException("El usuario no tiene un presupuesto registrado para este mes.");
            }

            double presupuestoMensual = presupuesto.getCantidadLimite();
            if (presupuestoMensual <= 0) {
                throw new RuntimeException("El presupuesto mensual debe ser mayor que cero.");
            }

            // 4️⃣ Obtener todas las transacciones del usuario
            List<Movimientos> transacciones = repositoryTransaccion.findAllByUsuario(usuarioId);

            // 5️⃣ Filtrar las transacciones del mes actual y calcular el total gastado
            double totalGastado = transacciones.stream()
                    .filter(t -> t.getFecha() != null &&
                            t.getFecha().getMonth().equals(LocalDate.now().getMonth()) &&
                            t.getTipo() != null &&
                            t.getTipo() == Tipo.GASTO) // Filtrar solo gastos
                    .mapToDouble(Movimientos::getCantidad)
                    .sum();

            // 6️⃣ Calcular el porcentaje gastado con una verificación extra para evitar división por cero
            return (totalGastado / presupuestoMensual) * 100;

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    //hacer
    @Override
    public List<Presupuesto> findByPerfilId(long perfilId) {
        return (List<Presupuesto>) repositoryPresupuesto.findAllByUser(perfilId);
    }

    @Override
    public List<Presupuesto> presupuestoactualuser(long perfilId) {
        int currentMonth = LocalDate.now().getMonthValue();
        int currentYear = LocalDate.now().getYear();

        return repositoryPresupuesto.presupuestoactualuser(perfilId, currentMonth, currentYear);
    }

    @Override
    public Presupuesto findById(long id) {
        return repositoryPresupuesto.findById(id).orElseThrow(
                () -> new NotFoundEntityException(id,Presupuesto.class.getSimpleName()));
    }

    @Override
    public boolean Create(Double cantidadLimite, String descripcion,String presupuestomes, long idUser) {
        try {
            Perfil perfil = repositoryPerfil.findById(idUser)
                    .orElseThrow(() -> new NotFoundEntityException(idUser, Perfil.class.getSimpleName()));

            LocalDateTime fechaActual = LocalDateTime.now();

            // Obtener todos los presupuestos del usuario (deberás asegurarte que no haya más de un presupuesto al mes)
            List<Presupuesto> presupuestosExistentes = repositoryPresupuesto.findAllByUser(perfil.getId());

            // Verificar si ya existe un presupuesto para el mes actual
            for (Presupuesto presupuestoExistente : presupuestosExistentes) {
                // Extraemos solo el año y mes de la fecha del presupuesto existente y la comparamos
                LocalDateTime fechaPresupuesto = presupuestoExistente.getFecha();
                if (fechaPresupuesto.getYear() == fechaActual.getYear() &&
                        fechaPresupuesto.getMonth() == fechaActual.getMonth()) {
                    throw new IllegalStateException("Ya existe un presupuesto para el mes actual.");
                }
            }

            // Obtener la cuenta bancaria del usuario
            CuentaBancaria cuentaBancaria = perfil.getCuentaBancaria();
            if (cuentaBancaria == null) {
                throw new IllegalStateException("Cuenta bancaria no encontrada para el usuario con ID: " + idUser);
            }

            // Validar saldo suficiente
            if (cantidadLimite <= 0 || cuentaBancaria.getSaldo().compareTo(BigDecimal.valueOf(cantidadLimite)) < 0) {
                throw new IllegalStateException("La cantidad debe ser superior a 0");
            }

            // Crear y guardar el presupuesto
            Presupuesto presupuesto = new Presupuesto();
            presupuesto.setFecha(LocalDateTime.now());
            presupuesto.setPresupuestomes(presupuestomes);
            presupuesto.setCantidadLimite(cantidadLimite);
            presupuesto.setDescripcion(descripcion);
            presupuesto.setPerfil(perfil);

            repositoryPresupuesto.save(presupuesto);

            Auditoria auditoria = new Auditoria();
            auditoria.setPerfil(perfil);
            auditoria.setAccion("Creación presupuesto");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("El usuario " + perfil.getNombre() + " ha creado un presupuesto con cantidad límite "+presupuesto.getCantidadLimite());

            auditoriaRepository.save(auditoria);

            return true;
        } catch (Exception e) {
            throw new CreateEntityException("Error al crear el presupuesto: " + e.getMessage(), e);
        }
    }


    @Override
    public Presupuesto Update(long id, PresupuestoDTO presupuesto) {
        Presupuesto presupuesto1 = repositoryPresupuesto.findById(id).orElseThrow(
                () -> new NotFoundEntityException(id, Presupuesto.class.getSimpleName())
        );

        // Guardar la información actual del presupuesto antes de la actualización
        BigDecimal cantidadLimiteAnterior = BigDecimal.valueOf(presupuesto1.getCantidadLimite());
        String descripcionAnterior = presupuesto1.getDescripcion();

        presupuesto1.setCantidadLimite(presupuesto.getCantidadLimite());
        presupuesto1.setDescripcion(presupuesto.getDescripcion());

        try {
            // Registrar auditoría: Guardar un registro en la tabla de auditoría
            Auditoria auditoria = new Auditoria();
            auditoria.setPerfil(presupuesto1.getPerfil());  // ID del perfil (usuario que realiza la acción)
            auditoria.setAccion("UPDATE");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("Presupuesto actualizado. ID: " + id +
                    ", Limite anterior: " + cantidadLimiteAnterior +
                    ", Descripción anterior: " + descripcionAnterior +
                    ", Nuevo límite: " + presupuesto.getCantidadLimite() +
                    ", Nueva descripción: " + presupuesto.getDescripcion());

            // Guardar la auditoría en la base de datos (asumiendo que tienes un repositorio para 'auditoria')
            auditoriaRepository.save(auditoria);

            // Guardar el presupuesto actualizado
            return repositoryPresupuesto.save(presupuesto1);
        } catch (Exception e) {
            throw new UpdateEntityException("Error al modificar el presupuesto con identificador: " + id, e);
        }
    }


    @Override
    public void delete(long id) {
        try {
            Presupuesto presupuesto1 = repositoryPresupuesto.findById(id).orElseThrow(
                    () -> new NotFoundEntityException(id, Presupuesto.class.getSimpleName())
            );
            repositoryPresupuesto.deleteById(id);

            Auditoria auditoria = new Auditoria();
            auditoria.setPerfil(presupuesto1.getPerfil());  // ID del perfil (usuario que realiza la acción)
            auditoria.setAccion("DELETE");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("Presupuesto eliminado con id: " + id);

            // Guardar la auditoría en la base de datos (asumiendo que tienes un repositorio para 'auditoria')
            auditoriaRepository.save(auditoria);

        }catch (Exception e) {
            throw new DeleteEntityException("Error al borrar el presupuesto con identificador " + id, e);
        }
    }

    @Override
    public List<Presupuesto> findPresupuestoByPerfilAndFecha(Long perfilId, LocalDateTime fecha) {
        return (List<Presupuesto>) repositoryPresupuesto.findPresupuestoByPerfilAndFecha(perfilId, fecha);
    }

}
