package com.example.Gestor.Model.Repository;

import com.example.Gestor.Model.Entity.Presupuesto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IRepositoryPresupuesto extends CrudRepository<Presupuesto, Long> {

    //@Query("SELECT p FROM Presupuesto p WHERE p.perfil.id = :perfilId AND p.fecha = :fecha")
    List<Presupuesto> findPresupuestoByPerfilAndFecha(Long perfilId, LocalDateTime fecha);

    //@Query("select p from Presupuesto p where p.perfil.id =:id_User")
    List<Presupuesto> findAllByUser(long id_User);

    //@Query("select p.cantidadLimite from Presupuesto p where p.perfil =: id_User")
    List<Presupuesto> findCantidadLimiteByPerfilId(long idUser);

    //List<Presupuesto> findByPerfilId(long perfilId);
    List<Presupuesto> presupuestoactualuser(long perfilId, int currentMonth, int currentYear);
}
