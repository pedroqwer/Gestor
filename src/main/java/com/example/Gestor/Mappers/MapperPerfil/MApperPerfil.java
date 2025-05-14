package com.example.Gestor.Mappers.MapperPerfil;

import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.Model.Entity.Perfil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MApperPerfil {

    @Autowired
    private ModelMapper modelMapper;

    public PerfilDTO toDTO(Perfil perfil) {
        return modelMapper.map(perfil, PerfilDTO.class);
    }

    public List<PerfilDTO> toDTOL(List<Perfil> perfils) {
        return perfils.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
