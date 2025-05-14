package com.example.Gestor.Model.Service.Usuario;

import com.example.Gestor.Model.Entity.Rol;
import com.example.Gestor.Model.Repository.IUsuarioRepository.IRolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService implements IRoleService
{
    @Autowired
    IRolRepository roleRepository;

    @Override
    public List<Rol> obtenerRolesByNombre(List<Integer> nombre) {
        return roleRepository.findByNombreIn(nombre);
    }
}
