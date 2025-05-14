package com.example.Gestor.Model.Repository.IUsuarioRepository;

import com.example.Gestor.Model.Entity.Rol;
import com.example.Gestor.Model.Entity.RoleType;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Hidden
@Repository
public interface IRolRepository extends CrudRepository<Rol, Integer> {
    @Query(value = "SELECT * FROM role WHERE id IN (:rolenames)", nativeQuery = true)
    List<Rol> findByNombreIn(@Param("rolenames") List<Integer> rolenames);

    Optional<Rol> findByNombre(RoleType admin);
}
