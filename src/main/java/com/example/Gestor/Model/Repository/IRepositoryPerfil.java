package com.example.Gestor.Model.Repository;

import com.example.Gestor.Model.Entity.Perfil;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRepositoryPerfil extends CrudRepository<Perfil, Long> {
    @Query("SELECT p FROM Perfil p WHERE p.username = :username")
    Optional<Perfil> findByUsername(String username);
    Long obtenerIdByUsernameAndPass(String username, String password);
    String obtenertelefonobyid(long id);
}
