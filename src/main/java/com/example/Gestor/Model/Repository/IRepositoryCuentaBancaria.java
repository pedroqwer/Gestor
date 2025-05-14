package com.example.Gestor.Model.Repository;

import com.example.Gestor.Model.Entity.CuentaBancaria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRepositoryCuentaBancaria extends CrudRepository<CuentaBancaria, Long> {

    //@Query("SELECT c FROM CuentaBancaria c WHERE c.usuario = :usuario")
    Optional<CuentaBancaria> findByUsuario(Long usuario);

    boolean tieneCuenta(long usuarioId);
}
