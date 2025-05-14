package com.example.Gestor.Model.Service.Auditoria;

import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.Presupuesto;

import java.util.List;

public interface IAuditoriaService {
    Auditoria findById(long id);
    List<Auditoria> findAllByUserAuditoria(long id_User);

}
