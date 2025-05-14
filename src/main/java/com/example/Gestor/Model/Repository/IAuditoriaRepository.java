package com.example.Gestor.Model.Repository;

import com.example.Gestor.Model.Entity.Auditoria;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IAuditoriaRepository extends CrudRepository<Auditoria, Long> {

    List<Auditoria> findAllByUserAuditoria(long perfilId);

}
