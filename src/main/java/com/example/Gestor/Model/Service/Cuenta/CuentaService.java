package com.example.Gestor.Model.Service.Cuenta;

import com.example.Gestor.Exception.CreateEntityException;
import com.example.Gestor.Exception.CuentaNoEncontradaException;
import com.example.Gestor.Exception.NotFoundEntityException;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.CuentaBancaria;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Repository.IAuditoriaRepository;
import com.example.Gestor.Model.Repository.IRepositoryCuentaBancaria;
import com.example.Gestor.Model.Repository.IRepositoryPerfil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CuentaService implements ICuentaService{

    @Autowired
    private IRepositoryCuentaBancaria cuentaBancariaRepository;

    @Autowired
    private IRepositoryPerfil perfilRepository;

    @Autowired
    private IAuditoriaRepository auditoriaRepository;
    Auditoria auditoria = new Auditoria();

    @Override
    public boolean crearCuentaBancaria(Long usuarioId, BigDecimal saldoInicial, String moneda) {
        try {
            Perfil perfil = perfilRepository.findById(usuarioId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con ID: " + usuarioId));

            Optional<CuentaBancaria> cuentaExistente = cuentaBancariaRepository.findByUsuario(perfil.getId());
            if (cuentaExistente.isPresent()) {
                throw new IllegalStateException("El usuario ya tiene una cuenta bancaria.");
            }

            CuentaBancaria cuenta = new CuentaBancaria();
            cuenta.setUsuario(perfil);
            cuenta.setSaldo(saldoInicial);
            cuenta.setMoneda(moneda);

            auditoria.setPerfil(perfil);
            auditoria.setAccion("Creación cuenta");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("El usuario " + perfil.getNombre() + " ha creado una cuenta bancaria.");

            auditoriaRepository.save(auditoria);

            cuentaBancariaRepository.save(cuenta);

            return true;
        }catch (Exception e) {
            throw new CreateEntityException("Error al crear la cuenta: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean tieneCuenta(long usuarioId) {
        return cuentaBancariaRepository.tieneCuenta(usuarioId);
    }

    @Override
    public CuentaBancaria obtenerCuentaPorUsuario(Long usuarioId) {
            return cuentaBancariaRepository.findByUsuario(usuarioId)
                    .orElseThrow(() -> new CuentaNoEncontradaException("Cuenta no encontrada"));
    }

    /*
    @Override
    public CuentaBancaria depositar(Long usuarioId, BigDecimal cantidad) {
        CuentaBancaria cuenta = obtenerCuentaPorUsuario(usuarioId);
        cuenta.setSaldo(cuenta.getSaldo().add(cantidad));
        return cuentaBancariaRepository.save(cuenta);
    }

    @Override
    public CuentaBancaria retirar(Long usuarioId, BigDecimal cantidad) {
        CuentaBancaria cuenta = obtenerCuentaPorUsuario(usuarioId);

        if (cuenta.getSaldo().compareTo(cantidad) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente.");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(cantidad));
        return cuentaBancariaRepository.save(cuenta);
    }*/

    @Override
    public BigDecimal consultarSaldo(Long usuarioId) {
        return obtenerCuentaPorUsuario(usuarioId).getSaldo();
    }

    /*@Override
    public void eliminarCuenta(Long usuarioId) {
        try {
            cuentaBancariaRepository.deleteById(usuarioId);
        }catch (Exception e){
            throw new NotFoundEntityException(usuarioId,CuentaBancaria.class.getSimpleName());
        }
    }*/
}