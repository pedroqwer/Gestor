package com.example.Gestor.Model.Service.Usuario;

import com.example.Gestor.Model.Entity.Rol;

import java.util.List;

public interface IRoleService
{
    List<Rol> obtenerRolesByNombre(List<Integer> nombre);
}
