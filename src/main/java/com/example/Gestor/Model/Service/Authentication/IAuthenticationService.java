package com.example.Gestor.Model.Service.Authentication;

import com.example.Gestor.Model.Entity.Usuario;

public interface IAuthenticationService
{
    public Usuario signup(Usuario newUser);

    public String authenticate(Usuario user);
}
