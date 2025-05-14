package com.example.Gestor.Mappers.MapperCuenta;

import com.example.Gestor.DTO.Cuenta.CuentaDTO;
import com.example.Gestor.DTO.Cuenta.NewCuentaDTO;
import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.DTO.Presupuesto.NewPresupuestoDTO;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.CuentaBancaria;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Entity.Presupuesto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MapperCuenta {

    @Autowired
    private ModelMapper modelMapper;

    public CuentaDTO toDTO(CuentaBancaria cuentaBancaria) {
        return modelMapper.map(cuentaBancaria, CuentaDTO.class);
    }

    public List<CuentaDTO> toDTOL(List<CuentaBancaria> cuentaBancarias) {
        return cuentaBancarias.stream().map(this::toDTO).collect(Collectors.toList());
    }
    public CuentaBancaria fromDTO(NewCuentaDTO newCuentaDTO)
    {
        return modelMapper.map(newCuentaDTO, CuentaBancaria.class);
    }


}
