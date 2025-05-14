package com.example.Gestor.Model.Service.Auditoria;

import com.example.Gestor.Exception.NotFoundEntityException;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.Presupuesto;
import com.example.Gestor.Model.Repository.IAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService implements IAuditoriaService{

    @Autowired
    private IAuditoriaRepository auditoriaRepository;

    @Override
    public Auditoria findById(long id) {
        return auditoriaRepository.findById(id).orElseThrow(
                () -> new NotFoundEntityException(id, Auditoria.class.getSimpleName()));
    }
    @Override
    public List<Auditoria> findAllByUserAuditoria(long id_User) {
        return auditoriaRepository.findAllByUserAuditoria(id_User);
    }
}
