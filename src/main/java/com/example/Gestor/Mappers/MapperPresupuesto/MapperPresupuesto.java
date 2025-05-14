package com.example.Gestor.Mappers.MapperPresupuesto;

import com.example.Gestor.DTO.Presupuesto.NewPresupuestoDTO;
import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.Model.Entity.Presupuesto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperPresupuesto {

    @Autowired
    private ModelMapper modelMapper;

    public PresupuestoDTO toDTO(Presupuesto presupuesto) {
        return modelMapper.map(presupuesto, PresupuestoDTO.class);
    }

    public List<PresupuestoDTO> toDTOL(List<Presupuesto> presupuestos)
    {
        return presupuestos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Presupuesto fromDTO(NewPresupuestoDTO newPresupuestoDTO)
    {
        return modelMapper.map(newPresupuestoDTO, Presupuesto.class);
    }
}
