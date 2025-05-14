package com.example.Gestor.Mappers.MapperTransacion;

import com.example.Gestor.DTO.Transaccion.TransaccionDTO;
import com.example.Gestor.Model.Entity.Movimientos;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperTransaccion {

    @Autowired
    private ModelMapper modelMapper;

    public TransaccionDTO toDTO(Movimientos transacciones) {
        return modelMapper.map(transacciones, TransaccionDTO.class);
    }

    public List<TransaccionDTO> toDTOL(List<Movimientos> transacciones)
    {
        return transacciones.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Movimientos fromDTO(TransaccionDTO transaccionDTO)
    {
        return modelMapper.map(transaccionDTO, Movimientos.class);
    }
}
