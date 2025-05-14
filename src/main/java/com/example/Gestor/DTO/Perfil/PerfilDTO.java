package com.example.Gestor.DTO.Perfil;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PerfilDTO implements Serializable {
    private long id;
    private String nombre;
    private String apellido;
    private String username;
    private String password;
    private String telefono;
    private String email;
    private String dni;
    //private String ultimoLogin;
}
