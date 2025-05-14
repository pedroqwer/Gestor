package com.example.Gestor.DTO.Usuario;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CrearUsuarioDTO implements Serializable {

    public String nombre;
    public String apellido;
    public String username;
    public String password;
    public String email;
    public String dni;
    public String telefono;
    public List<Integer> roles;
}