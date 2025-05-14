package com.example.Gestor.Mappers.MapperAuditoria;

import com.example.Gestor.DTO.Auditroria.AuditoriaDTO;
import com.example.Gestor.DTO.Cuenta.CuentaDTO;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.CuentaBancaria;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperAuditoria {

    @Autowired
    private ModelMapper modelMapper;

    public AuditoriaDTO toDTO(Auditoria auditoria) {
        return modelMapper.map(auditoria, AuditoriaDTO.class);
    }

    public List<AuditoriaDTO> toDTOL(List<Auditoria> auditorias) {
        return auditorias.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
